/*-------------------------------------------------------
File: user.c

Name: Jimmy Deng
Student Number: 8194330

Description: This program is designed to test the severs
              program using pipes.  It simulates
	     a user and bot by spawning a child to act
	     as Bot, while the parent acts as User
	     .  To enable the dialog, two server
	     processes are spawned and connected using
	     two pipes.  An additional 2 pipes are used
	     to connect the User process to User
	     server standard input and the Bot
	     process to the other bot server standard
	     input.
--------------------------------------------------------*/
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/fcntl.h>

#include <stdlib.h>
#include <signal.h>
#include <unistd.h>
#include <string.h>

char *usermessages[] = /* User messages */
{
   "Need to recharge.",
   "    Selected : 1) Mobile",
   "    Selected : 2) Debit",
   "    Selected : 1) Confirm",
   "exit",
   NULL
};

char *botmessages[] = /* Bot messages */
{
   "1) Mobile",
   "2) Wireless",
   "1) Credit",
   "2) Debit",
   "1) Confirm",
   "2) Cancel",
   "Successful",
   "exit",
   NULL
};

char* server_exec_path = "build/server";
char* user_exec_path = "build/user";

int stdin_copy;
int stdout_copy;

/* function prototypes */
int setupBot(void);
void initBotServer(int, int [], int []);
int initUserServer(int [], int []);
void setupUser(int);


/*---------------------------------------------------------------
Function: main

Description:  The main function calls the following four functions:
                 setupBot: spawns a child to act as Bot.
		 initBotServer: spawns server process for
		                  servicing Bot
		 initUserServer: spawns server process for
		                  servicing User
		 setupUser: acts as User (no process is
		            spawned).

Assignment: Complete this function to ensure proper calls to
            the other functions.
---------------------------------------------------------------*/
int main(int argc, char *argv[])
{
    int perBotrqueue;  /* read end of pipe */
    int perUserwqueue;  /* write end of pipe */

    stdin_copy = dup(STDIN_FILENO);
    stdout_copy = dup(STDOUT_FILENO);

    printf("Simulation starting\n");

    perBotrqueue = setupBot(); /* setup Bot and its pipe */

    //test(perBotrqueue, 1);
    //return 0;

    //return 0;

    /* Create server for Bot*/
    /* note that stdin connected to Bot pipe */

    int server_fds[2];
    
    if (pipe(server_fds) == -1)
    {
        fprintf(stderr, "Pipe failed\n");
        perror("Pipe failed");
        exit(-1);
    }

    //close(server_fds[0]);
    //close(server_fds[1]);
    

    initBotServer(perBotrqueue, server_fds, 0); /*define the arguments*/
    /* Create Server for User*/
    /* note that stdin connected to persBot pipe */

    //sleep(50); return 0;

    perUserwqueue = initUserServer(server_fds, 0); /*define the arguments*/

    sleep(2);

    //return 0;

    /* Lets do User now */
    setupUser(perUserwqueue);
    
    sleep(5);

    printf("Simulation complete\n");

    
    close(stdin_copy);
    close(stdout_copy);
    close(perBotrqueue);
    close(perUserwqueue);
    close(server_fds[0]);
    close(server_fds[1]);


    return 0;
}

/*---------------------------------------------------------------
Function: setupBot

Description: This function spawns a child process to act as
              Bot.  It must create a pipe and attach the
	     write end of the pipe to the standard output of the
             Bot process. It returns the file descriptor of
             the read end so that it may be attached to the
	     standard input of a server process.


Assignment: Complete this function.  The code for generating
            messages has been provided.
---------------------------------------------------------------*/
int setupBot(void) // generates output -> pipe
{
    int fd[2];

    if (pipe(fd) == -1)
    {
        fprintf(stderr, "Pipe failed\n");
        perror("Pipe failed");
        exit(-1);
    }




    if (fork() == 0) // child
    {
        //close(1);
        dup2(fd[1], STDOUT_FILENO);
        close(fd[0]);
        

        sleep(6);  /* wait for first message from User*/
        printf("Select one option \n");

        for(int i=0 ; botmessages[i] != NULL ; i++)
        {
            printf("%s (%d)\n",botmessages[i], getpid());
            if (botmessages[i+1] != NULL)
            {
                printf("%s (%d)\n",botmessages[i+1], getpid());
                i++;
            }
            fflush(stdout);
            sleep(4);  /* wait for response */
        }
        sleep(2);

        //close(1);
        close(fd[1]);

        exit(0);
    }

    close(fd[1]);

    return fd[0];
}

/*---------------------------------------------------------------
Function: initBotServer

Description: This function spawns a server process for
              Bot.  Two arguments are provided to the called
	     server program, the file descriptors for reading
	     and writing to the server servicing User.
	     It must also attach read end of a pipe attached
	     to  Bot process to its standard input.

Assignment: Complete this function.
---------------------------------------------------------------*/
void initBotServer(int fd, int server_fds[], int c[]) /*define the parameters*/
{
    pid_t pid = fork();

    if (pid == 0)
    {
        int fd_in = fd;
        int fd_out = server_fds[1];

        fd_out = STDOUT_FILENO;

        //close(0);
        dup2(fd, STDIN_FILENO);
        //close(fd);

        //dup2(stdout, 1);
        //dup2(stdout_copy, 1);

        sleep(2);

        // set up input_str for read end of pipe
        int length = snprintf(NULL, 0, "%d", fd_in);
        char* input_str = (char*)malloc(length + 1);
        snprintf(input_str, length + 1, "%d", fd_in);

        // set up output_str for write end of pipe
        length = snprintf(NULL, 0, "%d", fd_out);
        char* output_str = (char*)malloc(length + 1);
        snprintf(output_str, length + 1, "%d", fd_out);


        execlp(server_exec_path, server_exec_path, input_str, output_str, NULL);


        //free(input_str);
        //free(output_str);

        //kill(pid, SIGTERM);

        //exit(0);
    }

}


/*---------------------------------------------------------------
Function: initUserServer

Description: This function spawns a server process for
             User.  Two arguments are provided to the called
	     server program, the file descriptors for reading
	     and writing to the server servicing Bot.
	     It must also create a new pipe and attach the read
	     end of the new pipe to the standard input of the
	     server process. It shall return the write
	     end of the pipe to be attached to the User
	     process.

Assignment: Complete this function.
---------------------------------------------------------------*/
int initUserServer(int server_fds[], int b[])
{  
    int fd[2];
    if (pipe(fd) == -1)
    {
        perror("Pipe failed");
        exit(-1);
    }

    //fd[1] = 2;

    pid_t pid = fork();
    if (pid == 0)
    {
        //int fd_in = server_fds[0];
        //int fd_out = server_fds[1];

        //fd_out = 1;
        close(fd[1]);
        fd[1] = STDOUT_FILENO;

        dup2(fd[0], STDIN_FILENO);
        //close(fd[0]);
        //close(fd[1]);

        //dup2(stdout_copy, 1);

        sleep(2);

        // set up input_str for read end of pipe
        int length = snprintf(NULL, 0, "%d", fd[0]);
        char* input_str = (char*)malloc(length + 1);
        snprintf(input_str, length + 1, "%d", fd[0]);

        // set up output_str for write end of pipe
        length = snprintf(NULL, 0, "%d", fd[1]);
        char* output_str = (char*)malloc(length + 1);
        snprintf(output_str, length + 1, "%d", fd[1]);

        execlp(server_exec_path, server_exec_path, input_str, output_str, NULL);

        //free(input_str);
        //free(output_str);

        //kill(pid, SIGTERM);

        //exit(0);
    }

    close(fd[0]);
    //close(fd[1]);

    return fd[1];
}

/*---------------------------------------------------------------
Function: setupUser

Description: This function acts as user by sending the
             User messages to the standard output.  The
	     standard output is attached to the pipe
	     attached to the server standard input servicing
	     User.

Assignment: Complete this function.  The code for generating
            messages has been provided.
---------------------------------------------------------------*/
void setupUser(int persUserwfd)
{
    /* Do the User conversation */

    //close(1);
    dup2(persUserwfd, STDOUT_FILENO);
    //close(persUserwfd);

    /* now we can start the conversation */
    sleep(2);  /* wait before sending first message to Bot */
    for(int i=0 ; usermessages[i] != NULL ; i++)
    {
        printf("%s (%d)\n",usermessages[i], getpid());
        fflush(stdout);
        sleep(4);  /* adjust sleep so that message is sent after two messages */
    }
    /* conversation done */
    
    sleep(2);

    close(STDOUT_FILENO);
    close(persUserwfd);

    dup2(stdout_copy, STDOUT_FILENO);
}
