BUILD_DIR = build
CC = gcc -Wall -g

all: user server

user: user.c
	$(CC) -o $(BUILD_DIR)/user user.c

server: server.c
	$(CC) server.c -o $(BUILD_DIR)/server -lpthread

clean:
	rm -f $(BUILD_DIR)/*

gdb:
	gdb -x debug.gdb $(BUILD_DIR)/user