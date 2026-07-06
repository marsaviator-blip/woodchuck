package org.woodchuck.zChecker.controllers;
    
// import org.springframework.ai.chat.model.ChatModel;
// import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
// import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
// import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
// import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/audio")
public class AudioTranscriptionController {

    // private final OpenAiAudioTranscriptionModel transcriptionModel;

    // public AudioTranscriptionController(OpenAiAudioTranscriptionModel transcriptionModel) {
    //     this.transcriptionModel = transcriptionModel;
    // }

//    @PostMapping("/transcribe")
//    public String transcribeAudio(@RequestParam("file") MultipartFile file) {
        // Configure options like the specific model or language format
//        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
             //   .withLanguage("en")
             //   .withResponseFormat(OpenAiAudioTranscriptionOptions.TranscriptResponseFormat.TEXT)
//                .build();

        // Create the prompt wrapping the file's resource and configurations
//        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(file.getResource(), options);
        
        // Execute the transcription call
//        AudioTranscriptionResponse response = transcriptionModel.call(prompt);
        
//        return response.getResult().getOutput();
//    }
}

