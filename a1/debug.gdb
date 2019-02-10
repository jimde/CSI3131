winheight src 20

set pagination off

#set follow-fork-mode child
#set follow-fork-mode parent
#set follow-exec-mode new
#set follow-exec-mode same
#set detach-on-fork off

break user.c:main
break server.c:main
break server.c:talk
break server.c:generateThreads

run