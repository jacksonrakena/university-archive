cmake_minimum_required(VERSION 3.24)
project(files C)

set(CMAKE_C_STANDARD 11)

include_directories(.)

add_executable(t1
        t1test.c dbms.c)
add_executable(t2 t2test.c dbms.c)
add_executable(t3 t3test.c dbms.c)