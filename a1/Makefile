BUILD_DIR = build
CC = gcc -Wall -g

all: user server

user: user.c
	$(CC) -o $(BUILD_DIR)/user user.c

server: server.c
	$(CC) server.c -o $(BUILD_DIR)/server -lpthread

clean:
	rm -f $(BUILD_DIR)/*

gdb: all
	gdb -x debug.gdb $(BUILD_DIR)/user

run: clean all
	build/user

valgrind: all
	valgrind --trace-children=yes --track-fds=yes build/user

watch:
	watch -n 1 --color "ps -ef | egrep --color 'build|\[server\]'"