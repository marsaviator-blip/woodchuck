#include <vector>
#include <string>
#include <unordered_set>
#include <unordered_map>
#include <memory>

// --- BERTopic C++ Payload ---
struct BerTopicPayload {
    std::string note_id;                  // The FCA Object (G)
    int assigned_topic_id;                // Cluster metric reference
    std::vector<std::string> top_keywords; // Top c-TF-IDF terms -> FCA Attributes (M)
};

// --- FCA Binary Relation Matrix Matrix ---
class FormalContextTable {
private:
    std::vector<std::string> objects; // Row Headers (G)
    std::vector<std::string> attributes; // Column Headers (M)
    
    // Fast index lookups
    std::unordered_map<std::string, size_t> obj_to_idx;
    std::unordered_map<std::string, size_t> attr_to_idx;

    // Flat 1D vector acting as a 2D dense binary grid (Rows * Cols)
    std::vector<uint8_t> binary_relation_matrix;

public:
    // Safe ingestion contract matching the BERTopic payload structure
    void ingest_payload(const BerTopicPayload& payload) {
        // 1. Ensure object (Row) is tracked
        if (obj_to_idx.find(payload.note_id) == obj_to_idx.end()) {
            objects.push_back(payload.note_id);
            obj_to_idx[payload.note_id] = objects.size() - 1;
        }
        size_t row_idx = obj_to_idx[payload.note_id];

        // 2. Scan and expand new attributes (Columns) dynamically
        bool structural_resize_needed = false;
        for (const auto& keyword : payload.top_keywords) {
            if (attr_to_idx.find(keyword) == attr_to_idx.end()) {
                attributes.push_back(keyword);
                attr_to_idx[keyword] = attributes.size() - 1;
                structural_resize_needed = true;
            }
        }

        // 3. Re-allocate matrix if new attribute dimensions were introduced
        if (structural_resize_needed) {
            rebuild_matrix_dimensions();
        }

        // 4. Set binary values to True (1) for this payload's relations
        size_t total_cols = attributes.size();
        for (const auto& keyword : payload.top_keywords) {
            size_t col_idx = attr_to_idx[keyword];
            size_t linear_index = (row_idx * total_cols) + col_idx;
            binary_relation_matrix[linear_index] = 1; 
        }
    }

    void debug_print_matrix() const {
        std::cout << "\n--- Current FCA Formal Context Matrix ---\n\t";
        for (const auto& attr : attributes) std::cout << attr << "\t";
        std::cout << "\n";

        size_t total_cols = attributes.size();
        for (size_t r = 0; r < objects.size(); ++r) {
            std::cout << objects[r] << "\t";
            for (size_t c = 0; c < total_cols; ++c) {
                std::cout << (binary_relation_matrix[(r * total_cols) + c] ? "X\t" : ".\t");
            }
            std::cout << "\n";
        }
    }

private:
    void rebuild_matrix_dimensions() {
        size_t total_rows = objects.size();
        size_t total_cols = attributes.size();
        
        // Temporary clean binary matrix storage
        std::vector<uint8_t> updated_matrix(total_rows * total_cols, 0);
        
        // Copy old cell positions over if they exist
        // Note: Real production code can optimize this with bitsets or sparse arrays
        binary_relation_matrix.swap(updated_matrix);
        binary_relation_matrix.resize(total_rows * total_cols, 0);
    }
};

int main() {
    FormalContextTable fca_table;

    // Simulate 2 independent user notes processed asynchronously by C++ BERTopic
    BerTopicPayload session_01{
        "Note_Block_101", 
        5, 
        {"Linear_Algebra", "Vector_Math"}
    };

    BerTopicPayload session_02{
        "Note_Block_102", 
        12, 
        {"Quantum_Computing", "Vector_Math"} // "Vector_Math" acts as the structural overlap bridge
    };

    fca_table.ingest_payload(session_01);
    fca_table.ingest_payload(session_02);
    fca_table.debug_print_matrix();

    return 0;
}
// Use code with caution.
// 2. High-Performance C++ Libraries for Bypassing Python
// To construct this entire pipeline without Python dependencies, use modern, native 
// hardware-accelerated C++ libraries that replicate BERTopic's components:
// A. For Transformer Embeddings 
// (Step 1)llama.cpp (libllama): Optimized for raw CPU/GPU execution without heavy dependencies. 
// You can pass small, open-source transformer models (like BGE-Small or MiniLM) to generate fast, local text embeddings.
// ONNX Runtime (C++ API): Ideal if you want to export standard Hugging Face sentence transformers 
// directly to an optimized .
// onnx graph file and run them using local hardware accelerators like CUDA or DirectML.
// B. For Dimensionality Reduction & Clustering 
// (Step 2 - UMAP/HDBSCAN Replacements)
// FAISS (Facebook AI Similarity Search): A highly optimized C++ vector library. 
// While not identical to UMAP, its high-performance K-Means clustering or Inverted File (IVF) 
// index generation scales gracefully to billions of vector points.
// mlpack: A versatile C++ machine learning library containing native implementations of DBSCAN 
// and Dual-tree HDBSCAN. It interfaces directly with standard Armadillo matrices, keeping 
// data transformations purely in fast C++ allocations.
// HNSWLib: A header-only C++ library for Hierarchical Navigable Small World graphs. 
// It is excellent for real-time background processing, letting you quickly map an incoming text block vector 
// to its closest conceptual neighborhood without doing a linear scan across millions of old elements.
// C. For c-TF-IDF Keyword Extraction 
// (Step 2 - Text Analysis)Simple Custom Tokenization 
// (std::string_view): Instead of a massive text framework, write a lightweight custom term-frequency parser 
// using std::string_view split pools and regular expressions to count local cluster frequencies against 
// global cluster counts.