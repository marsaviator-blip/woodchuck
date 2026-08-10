cmake_minimum_required(VERSION 3.20)
project(CategoricalMathEngine CXX)

# Enforce Modern C++ Standards
set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Source files
include_directories(include)
file(GLOB SOURCES "src/*.cpp")

# Build as a Shared Library (.so / .dylib / .dll) so Bun can call it via FFI
add_library(categorical_engine SHARED ${SOURCES})
