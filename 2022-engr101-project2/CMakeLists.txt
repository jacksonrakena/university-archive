cmake_minimum_required(VERSION 3.21)
project(Project_2)

set(CMAKE_CXX_STANDARD 11)

include_directories(.)

add_executable(Project_2
        video_proc.cpp
        video_proc.hpp)
