BUILD_DIR = build
CC = gcc -Wall -g

all: user server

user: user.c
	$(CC) -o user user.c

server: server.c
	$(CC) server.c -o server -lpthread

clean:
	rm -f server user

gdb: all
	gdb -x debug.gdb user

run: clean all
	./user

valgrind: all
	valgrind --trace-children=yes --track-fds=yes user

watch:
	watch -n 1 --color "ps -ef | egrep --color '\./server|\./user'"