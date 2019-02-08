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

    printf("Simulation starting\n");

    perBotrqueue = setupBot(); /* setup Bot and its pipe */

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
    else
    {
        fprintf(stderr, "Pipe created!\n");
    }
    //close(server_fds[0]);
    //close(server_fds[1]);


    initBotServer(perBotrqueue, server_fds, 0); /*define the arguments*/
    /* Create Server for User*/
    /* note that stdin connected to persBot pipe */

    //return 0;

    perUserwqueue = initUserServer(0, 0); /*define the arguments*/

    sleep(2);

    //return 0;

    /* Lets do User now */
    setupUser(perUserwqueue);

    fprintf(stderr,"Simulation complete\n");

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
    fprintf(stderr, "SETUPBOT()\n");
    int fd[2];

    if (pipe(fd) == -1)
    {
        fprintf(stderr, "Pipe failed\n");
        perror("Pipe failed");
        exit(-1);
    }
    else
    {
        fprintf(stderr, "Pipe created!\n");
    }



    if (fork() == 0) // child
    {
        fprintf(stderr, "child\n");

        //close(1);
        dup2(fd[1], 1);
        close(fd[0]);
        close(fd[1]);

        sleep(2);  /* wait for first message from User*/
        printf("Select one option \n");

        int i;
        for(i=0 ; botmessages[i] != NULL ; i++)
        {
            printf("%s (%d)\n",botmessages[i], getpid());
            fflush(stdout);
            sleep(2);  /* wait for response */
        }

    }
    else // parent
    {
        fprintf(stderr, "parent\n");
        //wait();
    }

    fprintf(stderr, "setupBot() fd: %d %d\n", fd[0], fd[1]);

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
    fprintf(stderr, "INITBOTSERVER()\n");
    /* Complete the function */

    if (fork() == 0)
    {
        int fd_read = server_fds[0];
        int fd_write = server_fds[1];

        close(0);
        dup2(fd, 0);
        //close(fd[0]);
        //close(fd[1]);

        sleep(2);

        // set up input_str for read end of pipe
        int length = snprintf(NULL, 0, "%d", fd_read);
        char* input_str = malloc(length + 1);
        snprintf(input_str, length + 1, "%d", fd_read);

        // set up output_str for write end of pipe
        length = snprintf(NULL, 0, "%d", fd_write);
        char* output_str = malloc(length + 1);
        snprintf(output_str, length + 1, "%d", fd_write);

        const char* args[] = {"build/server", input_str, output_str, NULL};

        fprintf(stderr, "before exec\n");

        execvp("build/server", args);

        free(input_str);
        free(output_str);

        fprintf(stderr, "asfsdafdfasdfs\n");
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
int initUserServer(int a[], int b[]) /*define the parameters*/
{
    fprintf(stderr, "INITUSERSERVER()\n");
    /* Complete the function */
    int fd[2];
    if (pipe(fd) == -1)
    {
        fprintf(stderr, "Pipe failed\n");
        perror("Pipe failed");
        exit(-1);
    }
    else
    {
        fprintf(stderr, "Pipe created!\n");
    }



    if (fork() == 0)
    {

        close(0);
        dup2(fd[0], 0);
        sleep(2);

        // set up input_str for read end of pipe
        int length = snprintf(NULL, 0, "%d", fd[0]);
        char* read_str = malloc(length + 1);
        snprintf(read_str, length + 1, "%d", fd[0]);

        // set up output_str for write end of pipe
        length = snprintf(NULL, 0, "%d", fd[1]);
        char* write_str = malloc(length + 1);
        snprintf(write_str, length + 1, "%d", fd[1]);

        const char* args[] = {"build/server", read_str, write_str, NULL};

        fprintf(stderr, "before exec\n");

        execvp("build/server", args);

        free(write_str);
        free(read_str);
    }



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
    fprintf(stderr, "SETUPUSER()\n");
    int i;
    /* Do the User conversation */

    close(1);
    dup2(persUserwfd, 1);

    /* now we can start the conversation */
    sleep(1);  /* wait before sending first message to Bot */
    for(i=0 ; usermessages[i] != NULL ; i++)
    {

       printf("%s (%d)\n",usermessages[i], getpid());
       fflush(stdout);
       sleep();  /* adjust sleep so that message is sent after two messages */
    }
    /* conversation done */
}
