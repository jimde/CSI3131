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

// user and server executables
char *server_exec_path = "./server";
char *user_exec_path = "./user";

// stdin and stdout fd copies
int stdin_copy;
int stdout_copy;

// server pids
pid_t user_server;
pid_t bot_server;

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

    // create backups of stdin and stdout
    stdin_copy = dup(STDIN_FILENO);
    stdout_copy = dup(STDOUT_FILENO);

    printf("Simulation starting\n");

    /* setup Bot and its pipe */
    perBotrqueue = setupBot();

    // set up server pipes
    int server_fds1[2], server_fds2[2];
    if (pipe(server_fds1) == -1 || pipe(server_fds2) == -1)
    {
        perror("Pipe failed");
        exit(-1);
    }

    /* Create server for Bot*/
    /* note that stdin connected to Bot pipe */
    initBotServer(perBotrqueue, server_fds1, server_fds2);

    /* Create Server for User*/
    /* note that stdin connected to persBot pipe */
    perUserwqueue = initUserServer(server_fds1, server_fds2);

    sleep(2);

    /* Lets do User now */
    setupUser(perUserwqueue);

    sleep(3);

    printf("Simulation complete\n");

    // fd cleanup
    close(perBotrqueue);
    close(perUserwqueue);
    close(server_fds1[0]);
    close(server_fds1[1]);
    close(server_fds2[0]);
    close(server_fds2[1]);
    close(STDIN_FILENO);
    close(STDOUT_FILENO);
    dup2(stdin_copy, STDIN_FILENO);
    dup2(stdout_copy, STDOUT_FILENO);
    close(stdin_copy);
    close(stdout_copy);

    // prevent zombie process
    kill(user_server, SIGTERM);
    kill(bot_server, SIGTERM);

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
int setupBot(void)
{
    int fd[2];

    if (pipe(fd) == -1)
    {
        perror("Pipe failed");
        exit(-1);
    }

    if (fork() == 0)
    {
        dup2(fd[1], STDOUT_FILENO);
        close(fd[0]);
        close(fd[1]);

        sleep(6);  /* wait for first message from User*/

        printf("Select one option \n");

        int i;
        for (i = 0 ; botmessages[i] != NULL; i++)
        {
            printf("%s (%d)\n", botmessages[i], getpid());
            if (botmessages[i+1] != NULL)
            {
                printf("%s (%d)\n", botmessages[i+1], getpid());
                i++;
            }
            fflush(stdout);
            sleep(4);  /* wait for response */
        }
        sleep(3);

        close(STDOUT_FILENO);

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
void initBotServer(int bot_fd, int server_fds1[], int server_fds2[])
{
    bot_server = fork();
    if (bot_server == 0)
    {
        dup2(server_fds1[0], STDIN_FILENO); // read from user server
        close(server_fds1[0]);
        dup2(stdout_copy, STDOUT_FILENO); // write to stdout

        sleep(2);

        // set up input_str for read end of pipe
        int length = snprintf(NULL, 0, "%d", bot_fd); // read from fd connected to bot
        char *input_str = (char *)malloc(length + 1);
        snprintf(input_str, length + 1, "%d", bot_fd);

        // set up output_str for write end of pipe
        length = snprintf(NULL, 0, "%d", server_fds2[1]); // write to fd connected to user server
        char *output_str = (char *)malloc(length + 1);
        snprintf(output_str, length + 1, "%d", server_fds2[1]);

        execlp(server_exec_path, server_exec_path, input_str, output_str, NULL);
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
int initUserServer(int server_fds1[], int server_fds2[])
{
    int fd[2];
    if (pipe(fd) == -1)
    {
        perror("Pipe failed");
        exit(-1);
    }

    user_server = fork();
    if (user_server == 0)
    {
        dup2(server_fds2[0], STDIN_FILENO); // read from fd connected to bot server
        close(server_fds2[0]);
        dup2(stdout_copy, STDOUT_FILENO); // write to stdout

        close(fd[1]);

        sleep(2);

        // set up input_str for read end of pipe
        int length = snprintf(NULL, 0, "%d", fd[0]); // read from fd connected to user
        char *input_str = (char *)malloc(length + 1);
        snprintf(input_str, length + 1, "%d", fd[0]);

        // set up output_str for write end of pipe
        length = snprintf(NULL, 0, "%d", server_fds1[1]); // write to fd connected to bot server
        char *output_str = (char *)malloc(length + 1);
        snprintf(output_str, length + 1, "%d", server_fds1[1]);

        execlp(server_exec_path, server_exec_path, input_str, output_str, NULL);
    }

    close(fd[0]);

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
    dup2(persUserwfd, STDOUT_FILENO);
    close(persUserwfd);

    /* now we can start the conversation */
    sleep(2);  /* wait before sending first message to Bot */

    int i;
    for(i = 0 ; usermessages[i] != NULL ; i++)
    {
        printf("%s (%d)\n",usermessages[i], getpid());
        fflush(stdout);
        sleep(4);  /* adjust sleep so that message is sent after two messages */
    }
    /* conversation done */

    sleep(3);

    close(STDOUT_FILENO);
    dup2(stdout_copy, STDOUT_FILENO);
}
