cmake_minimum_required(VERSION 3.22.1)
project(freyja-llama-stub)

add_library(
    llama
    SHARED
    native-lib.cpp
)

find_library(
    log-lib
    log
)

target_link_libraries(
    llama
    ${log-lib}
)