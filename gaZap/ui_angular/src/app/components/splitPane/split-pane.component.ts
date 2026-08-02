import { Component, signal, viewChild, ElementRef, effect } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { HttpClient } from "@angular/common/http";
import { SplitComponent, SplitAreaComponent } from "angular-split";

interface ChatMessage {
  prompt: string;
  isUser: boolean;
  isNote?: boolean;
  timestamp: Date;
  userId: string; // Track who sent it
  id: number; // Unique identifier for each message
}

@Component({
  selector: "app-split-pane",
  standalone: true,
  imports: [CommonModule, FormsModule, SplitComponent, SplitAreaComponent],
  templateUrl: "./split-pane.component.html",
})
export class SplitPaneComponent {
  protected readonly title = signal("angular-split");
  private apiUrl = "http://localhost:8089/api/chat/postStream"; // Your Spring Boot endpoint

  currentUserId = signal<string>("Developer_" + Math.floor(Math.random() * 1000));
  isAiThinking = signal<boolean>(false);
  userInput = signal<string>("");
  savedWorkspaceItems = signal<string[]>([]);
  messages = signal<ChatMessage[]>([
    {
      prompt: "Hello! How can I help you?",
      isUser: false,
      timestamp: new Date(),
      userId: this.currentUserId(),
      id: Date.now(), // Unique ID for each message
    },
  ]);
  inputMode = signal<"chat" | "note">("chat");

  private scrollContainer = viewChild<ElementRef<HTMLDivElement>>("scrollContainer");
  expandedState = signal<Record<number, boolean>>({});

  constructor(private http: HttpClient) {
    effect(() => {
      this.messages();
      setTimeout(() => {
        const container = this.scrollContainer()?.nativeElement;
        if (container) {
          container.scrollTo({
            top: container.scrollHeight,
            behavior: "smooth", // Adds a smooth sliding animation downward
          });
        }
      }, 50);
    });
  }

  handleSubmit(event: Event) {
    event.preventDefault();
    const textToProcess = this.userInput().trim();
    if (!textToProcess) return;
    console.log(textToProcess);
    if (this.inputMode() === "note") {
      // Force a fresh array instantiation to trigger Angular's reactive tracking
      const personalNote: ChatMessage = {
        prompt: textToProcess,
        isUser: false,
        isNote: true,
        timestamp: new Date(),
        userId: this.currentUserId(),
        id: Date.now(), // Unique ID for each note
      };

      // This updates the left pane stream directly
      this.messages.update((prev) => [...prev, personalNote]);
      this.userInput.set(""); // Clears the text box
    } else {
      this.sendChatMessage(textToProcess);
    }
  }

  private async sendChatMessage(textToSend: string) {
    this.isAiThinking.set(true);
    const userMessageId = Date.now() + Math.random();
    const userMessage: ChatMessage = {
      prompt: textToSend,
      isUser: true,
      timestamp: new Date(),
      userId: this.currentUserId(),
      id: userMessageId,
    };
    console.log("Sending user message:", userMessage);
    this.messages.update((prev) => [...prev, userMessage]);
    this.userInput.set("");

    const aiMessageId = Date.now() + 1;
    const aiMessagePlaceholder: ChatMessage = {
      prompt: "",
      isUser: false,
      timestamp: new Date(),
      userId: "Spring_AI",
      id: aiMessageId,
    };
    this.messages.update((prev) => [...prev, aiMessagePlaceholder]);

    try {
      const response = await fetch("http://localhost:8089/api/chat/postStream", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(userMessage), // Sending the full object wrapper
      });

      if (!response.body) return;

      this.isAiThinking.set(false);
      const reader = response.body.getReader();
      const decoder = new TextDecoder("utf-8");
      let accumulatedText = "";

      while (true) {
        const { value, done } = await reader.read();
        if (done) break;

        const chunk = decoder.decode(value, { stream: true });

        // Parse the standard "data: " prefix injected by Spring's ServerSentEvent DTO
        const lines = chunk.split("\n");
        for (const line of lines) {
          if (line.startsWith("data:")) {
            const cleanToken = line.replace("data:", "").trim();
            accumulatedText += cleanToken;
            // this.messages.update((prev) => {
            //   const updated = [...prev];
            //   if (updated.length > 0) {
            //     updated[updated.length - 1] = {
            //       ...updated[updated.length - 1],
            //       prompt: accumulatedText,
            //     };
            //   }
            //   return updated;
            this.messages.update((prev) =>
              prev.map(msg => msg.id === aiMessageId ? { ...msg, prompt: accumulatedText } : msg)
            );
          }
        }
      }
      this.isAiThinking.set(false);

    } catch (err) {
      console.error("Streaming connection error:", err);
      this.isAiThinking.set(false);
      this.messages.update((prev) => {
        const updated = [...prev];
        updated[updated.length - 1].prompt = "Error connection lost to backend.";
        return updated;
      });
    }
  }

  toggleMessageExpand(msgId: number, event: Event) {
    event.stopPropagation();
    this.expandedState.update(state => ({
      ...state,
      [msgId]: !state[msgId]
    }));
  }

  copyFullMessage(text: string) {
    this.savedWorkspaceItems.update((prev) => [...prev, text]);
  }

  copySelectedText(fullMessageText: string) {
    const selection = window.getSelection();
    const selectedText = selection ? selection.toString().trim() : "";

    // Safety check: ensure the highlighted text actually belongs to this message bubble context
    if (selectedText && fullMessageText.includes(selectedText)) {
      this.savedWorkspaceItems.update((prev) => [...prev, selectedText]);
      if (selection) {
        selection.removeAllRanges(); // Clear selection highlight after copying
      }
    } else {
      // Fallback if they clicked the button without highlighting anything specific
      this.savedWorkspaceItems.update((prev) => [...prev, fullMessageText]);
    }
  }
}
