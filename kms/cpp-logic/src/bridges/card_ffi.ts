// card_ffi.ts
import { dlopen, FFIType } from "bun:ffi";

// Map the compiled schema library to Bun
const lib = dlopen("./libstreamreader.so", {
  read_session_stream: {
    args: [
      FFIType.cstring, // host
      FFIType.i32,     // port
      FFIType.cstring, // session_id
      FFIType.cstring, // environment
      FFIType.cstring, // data_type
      FFIType.cstring, // item_id
    ],
    returns: FFIType.void,
  },
});

// Helper to sanitize buffers for C-string requirements (\0)
const toCString = (str: string) => Buffer.from(`${str}\0`);

// Configuration pointing to your exact keyspace examples
const host = toCString("127.0.0.1");
const port = 6379;

// Example Target: kms:session-1788298400881:developer_alpha:ai-response:response-1788298343636
const sessionId = toCString("1788298400881");
const environment = toCString("developer_alpha");
const dataType = toCString("ai-response");
const itemId = toCString("response-1788298343636");

console.log("Passing keyspace parameters to C++ FFI module...");
lib.symbols.read_session_stream(host, port, sessionId, environment, dataType, itemId);
