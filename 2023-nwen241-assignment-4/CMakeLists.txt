cmake_minimum_required(VERSION 3.22)
project(Assignment_4)

set(CMAKE_CXX_STANDARD 14)

include_directories(.)

add_executable(T3_Test
        abstractdb.cpp
        abstractdb.hpp
        t3test.cpp
        vectordb.cpp
        vectordb.hpp)

add_executable(T4_Test
        abstractdb.cpp
        abstractdb.hpp
        t4test.cpp
        vectordb.cpp
        vectordb.hpp)

add_executable(T5_Test
        abstractdb.cpp
        abstractdb.hpp
        t5test.cpp
        vectordb.cpp
        vectordb.hpp)
