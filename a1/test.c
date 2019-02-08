#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/fcntl.h>
#include <pthread.h>


void* myThreadFunc()
{
    sleep(2);
    printf("myThreadFunc!\n");
}

int main(int argc, char* argv[])
{
    printf("1\n");
    printf("fork:%d\n", fork());
    printf("2\n");

    pthread_t thread_id;
    printf("before thread\n");
    pthread_create(&thread_id, NULL, myThreadFunc, NULL);
    pthread_join(thread_id, NULL);
    printf("after thread\n");

    return 0;
}