The submitjob.pl script submits the job given by the first argument with the parameters given by the second argument.   This can be useful when it's difficult to keep tract of all the submission time parameters in use for a job.  The `Main.spl` file contains a sample application with three submission time parameters, `title`, `author`, and `year`.  The application just prints out the value of its parameters.

For example, you can do: 
`streamtool submitjob output/Main.sab -P author=Homer -P title='The Iliad' year=-1194`
but you might perfer to keep the submission time values in a file, `iliad.txt`:
```
author=Homer
title=The Iliad
year=-1194
```
This script lets you do that.  You can do
`submitjob.pl output/Main.sab iliad.txt` and it will automatically parse the file and add the specified parameters to the command line.  

You can use additional streamtool arguments by putting them after the file name, eg:
`submitjob.pl output/Main.sab iliad.txt -i instancename -d domainname`
See the `testStandalone.sh` and `testDistributed.sh` for example uses.

