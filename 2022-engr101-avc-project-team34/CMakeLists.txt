cmake_minimum_required(VERSION 3.21)
project(Project_3)

set(CMAKE_CXX_STANDARD 11)
add_definitions(-DCMAKE=1)

add_executable(Project_3
        main.cpp)

add_custom_command(
        TARGET Project_3 POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy
        ${CMAKE_SOURCE_DIR}/display.html
        ${CMAKE_CURRENT_BINARY_DIR}/)

add_custom_command(
        TARGET Project_3 POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy
        ${CMAKE_SOURCE_DIR}/maze.txt
        ${CMAKE_CURRENT_BINARY_DIR}/)