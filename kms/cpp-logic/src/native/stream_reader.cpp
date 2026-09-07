#include <sw/redis++/redis++.h>
#include <iostream>
#include <string>
#include <vector>
#include <unordered_map>

using namespace sw::redis;

extern "C" {
    // Exported function targeting the structured Dragonfly key namespace
    void read_session_stream(
        const char* host, 
        int port, 
        const char* session_id,
        const char* environment,
        const char* data_type,
        const char* item_id
    ) {
        try {
            // Establish Dragonfly client connection
            auto dragonfly = Redis("tcp://" + std::string(host) + ":" + std::to_string(port));
            
            // Construct the deterministic structured key 
            // Layout: kms:session-[session_id]:[env]:[type]:[item_id]
            std::string stream_key = "kms:session-" + std::string(session_id) + ":" 
                                   + std::string(environment) + ":" 
                                   + std::string(data_type) + ":" 
                                   + std::string(item_id);

            std::cout << "[C++] Querying stream: " << stream_key << std::endl;

            // Target structure for modern sw/redis++ stream consumption
            std::vector<std::pair<std::string, std::unordered_map<std::string, std::string>>> entries;
            
            // XREAD reading the top latest entry starting from the absolute beginning ("0")
            dragonfly.xread(stream_key, "0", 1, std::back_inserter(entries));

            if (entries.empty()) {
                std::cout << "[C++] No entries discovered in this stream payload." << std::endl;
                return;
            }

            // Iterate and print structured contents
            for (const auto &entry : entries) {
                const std::string &msg_id = entry.first;
                const auto &fields = entry.second;

                std::cout << "[C++] Stream Entry ID: " << msg_id << std::endl;
                for (const auto &field_pair : fields) {
                    std::cout << "  -> " << field_pair.first << " : " << field_pair.second << std::endl;
                }
            }
        } catch (const Error &e) {
            std::cerr << "[C++ Error] Dragonfly execution failed: " << e.what() << std::endl;
        }
    }
}
