cmake_minimum_required(VERSION 2.8)
project(GraphTraversalAlgorithms Java)

find_package(Java REQUIRED)
include(UseJava)

file(GLOB SOURCES "src/*.java")
add_jar(GraphTraversalAlgorithms ${SOURCES} ENTRY_POINT GraphTraversalFrame)

