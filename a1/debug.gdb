winheight src 20

set pagination off

#set follow-fork-mode child

break user.c:main
break server.c:main
break server.c:talk
break server.c:generateThreads

run