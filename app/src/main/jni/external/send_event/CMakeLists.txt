cmake_minimum_required(VERSION 3.6)

project(send_event C)

set(CMAKE_C_STANDARD 99)
set(CMAKE_C_STANDARD_REQUIRED ON)
#set(BUILD_SHARED_LIBS OFF) # 添加这一行强制使用静态库
set(BUILD_SHARED_LIBS OFF CACHE BOOL "Build static libs only")

# Android-specific config
if (ANDROID)
    message(STATUS "Target: Android")
    set(CMAKE_SYSTEM_NAME Android)
    set(CMAKE_SYSTEM_VERSION 21)
    set(CMAKE_ANDROID_ARCH_ABI arm64-v8a)
    set(CMAKE_ANDROID_NDK $ENV{ANDROID_NDK})
    set(CMAKE_ANDROID_STL_TYPE c++_static)
endif()



# Define target
add_executable(send_event send_event.c)

# Include json-c headers explicitly
target_include_directories(send_event PRIVATE
        ${CMAKE_SOURCE_DIR}/external/json-c
        ${CMAKE_SOURCE_DIR}/external/json-c/json-c
)

# Link json-c statically
target_link_libraries(send_event
        PRIVATE
        json-c
        log
)

set_target_properties(send_event PROPERTIES
        RUNTIME_OUTPUT_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}
)

message(STATUS "send_event will be at: ${CMAKE_CURRENT_BINARY_DIR}/send_event")