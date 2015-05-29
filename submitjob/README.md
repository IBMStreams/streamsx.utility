The submitjob.pl script submits the job given by the first argument with the parameters given by the second argument.  

submitjob.pl output/Main.sab iliad.txt

when iliad.txt is
author=Homer
title=The Iliad
year=-1194

is the same as 
streamtool submitjob output/Main.sab iliad.txt -P author=Homer -P title='The Iliad' year=-1194

You can use additional streamtool arguments by putting them after the file name, eg:

submitjob.pl output/Main.sab iliad.txt -i instancename -d domainname

See the testStandalone.sh and testDistributed.sh for example uses.

