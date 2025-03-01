cmake_minimum_required(VERSION 3.21)
project(project1)
set(CMAKE_OSX_ARCHITECTURES "arm64")
set(CMAKE_CXX_STANDARD 11)

add_executable(project1 wav.hpp challenge.cpp)
