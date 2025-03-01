cmake_minimum_required(VERSION 3.24)
project(Assignment_1 C)

set(CMAKE_C_STANDARD 11)

include_directories(.)

add_executable(t1tests editor.c t1test.c)
add_executable(t2tests editor.c t2test.c)
add_executable(t3tests
        editor.c
        t3test2.c)
add_executable(t4tests editor.c t4test.c)
