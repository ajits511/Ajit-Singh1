myjava code
#!/bin/bash
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/home/budi
ORACLE_SID=FIL
export ORACLE_SID
export ORACLE_HOME=/u01/app/oracle/product/12.1.0/dbhome_1
export LD_LIBRARY_PATH=$ORACLE_HOME/lib:$LD_LIBRARY_PATH
export PATH=$ORACLE_HOME/bin:$PATH
export ORACLE_USER="PROD_FIL_01"
export ORACLE_PSWD="FPtCCqBo3245"
#zic_prod_dump/DftnDftn2143
#recipients="prod-aim-support@aimsoftware.com"
recipients="ajit.singh@aimsoftware.com"
fullPath=$(readlink -f $0)
Path=$(dirname $fullPath)
LogPath=$Path/logs
emailHTML=$Path/html_files/FileMonitoringAlarms.html
dtCmd="date '+ %D %T'"
logFile="${0::${#0}-3}.`date '+%d%b%g'`"
logFile=$(echo "$logFile" |rev |cut -d"/" -f1|rev)


SQL_command()
{
        ResultTemp=`sqlplus -s /nolog << EOF
        conn ${ORACLE_USER}/${ORACLE_PSWD}@${ORACLE_SID}
        WHENEVER SQLERROR exit SQL.SQLCODE;
        SET FEEDBACK OFF VERIFY OFF HEADING OFF
        SET LINES 999
        Select '<tr><td>',FILEPATH as Feed_Name,'<\/td><td>' ,PROCESSINGFINISHED as Import_Time,'<\/td><td>',\
        ADDINFO as Status,'<\/td><\/tr>' from SA_FEED_RESPONSE where trunc(RESPONSEDATETIME)=trunc(sysdate)\
        and ADDINFO='Imported' and FILEPATH like '%$1%'  and trunc(PROCESSINGFINISHED)>=trunc(sysdate);
        exit;
	EOF`
        Result=$(echo $ResultTemp |tail -1)
        echo -e "$(eval $dtCmd) Query executed for $1"
        if [[ -n ${Result} ]]
        then
                echo -e "$(eval $dtCmd) data found for $1 "
        else
                echo -e "$(eval $dtCmd) no data found for $1"
        fi
}
send_mail()
{
echo -e "$1 file not imported. Please take appropriate action.\n\n\n\n----This is an auto generated mail. Please do not reply----"|mail -s "[FIL_PROD][$(eval $dtCmd)] $1 file is not imported" -r elk_mon_alert@aimsoftware.com $recipients
echo -e "$(eval $dtCmd) Email alert sent for $1"
}


send_mail_success()
{
        #set -x
  subject="[FIL_PROD][$(eval $dtCmd)] $1 price file has been imported successfully"
  sed -i "7s/.*/$2/" $emailHTML

  Body=`cat $emailHTML`
  Auto_Generate_Msg="**********This email is an auto-generated mail. Please do not reply**********"
  /usr/sbin/sendmail -f "elk_mon_alert@aimsoftware.com" -t<<EOM
To: $recipients
Subject : $subject
${Body}
${Auto_Generate_Msg}
EOM
if [ $? -eq 0 ]
then
        echo -e "$(eval $dtCmd) mail sent successfully"
fi
}

SQL_command "$2" >> $LogPath/$logFile 2>&1
if [[ -n ${Result} ]]
then
  #     echo $Result
        send_mail_success "$1" "$Result" >> $LogPath/$logFile 2>&1
else
        send_mail "$1" >> $LogPath/$logFile 2>&1
fi
#/!/bin/sh

reportdate=`date`
filedate=`date +"%Y%m%d"`

cd /opt/app/agilis/data/out/edw_temp/
alert=`ls -lrth *ALERT*dat | tail -1 |awk '{print " <td>ALERT </td> <td>"$8 " </td><td> "$6 " " $7 " </td><td> "$5 "</td>"}'`
audit=`ls -lrth *AUDIT*dat | tail -1 |awk '{print " <td>AUDIT </td> <td>"$8 " </td><td> "$6 " " $7 " </td><td> "$5 "</td>"}'`
case=`ls -lrth *CASE*dat | tail -1 |awk '{print "<td>CASE  </td> <td>"$8 " </td><td> "$6 " " $7 " </td><td> "$5 "</td>"}'`
edlstatus=`ls -lrth *EDLSTATUS*dat | tail -1 |awk '{print "<td>EDLSTATUS  </td> <td>"$8 " </td><td> "$6 " " $7 " </td><td> "$5 "</td>"}'`
cust=`ls -lrth *CUST*dat | tail -1 |awk '{print "<td>CUST  </td> <td>"$8 " </td><td> "$6 " " $7 " </td><td> "$5 "</td>"}'`
edlresp=`ls -lrth *EDLRESP_*dat | tail -1 |awk '{print "<td>EDLRESP  </td> <td>"$8 " </td><td> "$6 " " $7 " </td><td> "$5 "</td>"}'`
edlreq=`ls -lrth *EDLREQ_*dat | tail -1 |awk '{print "<td>EDLREQ  </td> <td>"$8 " </td><td> "$6 " " $7 " </td><td> "$5 "</td>"}'`
pfr=`ls -lrth *PFR_*dat | tail -1 |awk '{print "<td>PFR  </td> <td>"$8 " </td><td> "$6 " " $7 " </td><td> "$5 "</td>"}'`
defaulter=`ls -lrth *DEFAULTER_*dat | tail -1 |awk '{print "<td>DEFAULTER  </td> <td>"$8 " </td><td> "$6 " " $7 " </td><td> "$5 "</td>"}'`
oca=`ls -lrth *ORDERCREDITASSESSMENT_*dat | tail -1 |awk '{print "<td>ORDERCREDITASSESSMENT  </td> <td>"$8 " </td><td> "$6 " " $7 " </td><td> "$5 "</td>"}'`
app=`ls -lrth *APPLICATION_*dat | tail -1 |awk '{print "<td>APPLICATION  </td> <td>"$8 " </td><td> "$6 " " $7 " </td><td> "$5 "</td>"}'`
orders=`ls -lrth *ORDERS_*dat | tail -1 |awk '{print "<td>ORDERS  </td> <td>"$8 " </td><td> "$6 " " $7 " </td><td> "$5 "</td>"}'`
oderdev=`ls -lrth *ORDERDELIVERYSTATUS_*dat | tail -1 |awk '{print "<td>ORDERDELIVERYSTATUS  </td> <td>"$8 " </td><td> "$6 " " $7 " </td><td> "$5 "</td>"}'`
careqlog=`ls -lrth *CAREQLOG_*dat | tail -1 |awk '{print "<td>CRARELOG  </td> <td>"$8 " </td><td> "$6 " " $7 " </td><td> "$5 "</td>"}'`
aliasmatch=`ls -lrth *ALIASINGMATCHDETAIL_*dat | tail -1 |awk '{print "<td>ALIASINGMATCHDETAIL  </td> <td>"$8 " </td><td> "$6 " " $7 " </td><td> "$5 "</td>"}'`
fraud=`ls -lrth *FRAUDASSESSMENTSTATUS_*dat | tail -1 |awk '{print "<td>FRAUDASSESSMENTSTATUS  </td> <td>"$8 " </td><td> "$6 " " $7 " </td><td> "$5 "</td>"}'`

export tdate=`date "+%Y-%m-%d"`;

export edate=`date "+%Y%m%d"`;



cd /opt/app/agilis/data/out/out6
datacount=`ls -ltr *.dat|grep $tdate|wc -l`

cd /opt/app/agilis/bak/out6/
salert=`ls -lrth *ALERT*$filedate*dat.bak | awk '{print " <td> "$8 " </td><td> "$6 " " $7 "</td>"}'`
saudit=`ls -lrth *AUDIT*$filedate*dat.bak | awk '{print " <td> "$8 " </td><td> "$6 " " $7 "</td>"}'`
scase=`ls -lrth *CASE*$filedate*dat.bak | awk '{print " <td> "$8 " </td><td> "$6 " " $7 "</td>"}'`
sedlstatus=`ls -lrth *EDLSTATUS*$filedate*dat.bak | awk '{print " <td> "$8 " </td><td> "$6 " " $7 "</td>"}'`
scust=`ls -lrth *CUST*$filedate*dat.bak | awk '{print " <td> "$8 " </td><td> "$6 " " $7 "</td>"}'`
sedlresp=`ls -lrth *EDLRESP*$filedate*dat.bak | awk '{print " <td> "$8 " </td><td> "$6 " " $7 "</td>"}'`
sedlreq=`ls -lrth *EDLREQ*$filedate*dat.bak | awk '{print " <td> "$8 " </td><td> "$6 " " $7 "</td>"}'`
spfr=`ls -lrth *PFR*$filedate*dat.bak | awk '{print " <td> "$8 " </td><td> "$6 " " $7 "</td>"}'`
sdefaulter=`ls -lrth *DEFAULTER*$filedate*dat.bak | awk '{print " <td> "$8 " </td><td> "$6 " " $7 "</td>"}'`
soca=`ls -lrth *ORDERCREDITASSESSMENT_*$filedate*dat.bak | awk '{print " <td> "$8 " </td><td> "$6 " " $7 "</td>"}'`
sapp=`ls -lrth *APPLICATION_*$filedate*dat.bak | awk '{print " <td> "$8 " </td><td> "$6 " " $7 "</td>"}'`
sorders=`ls -lrth *ORDERS_*$filedate*dat.bak | awk '{print " <td> "$8 " </td><td> "$6 " " $7 "</td>"}'`
soderdev=`ls -lrth *ORDERDELIVERYSTATUS_*$filedate*dat.bak | awk '{print " <td> "$8 " </td><td> "$6 " " $7 "</td>"}'`
scareqlog=`ls -lrth *CAREQLOG_*$filedate*dat.bak | awk '{print " <td> "$8 " </td><td> "$6 " " $7 "</td>"}'`
saliasmatch=`ls -lrth *ALIASINGMATCHDETAIL_*$filedate*dat.bak | awk '{print " <td> "$8 " </td><td> "$6 " " $7 "</td>"}'`
sfraud=`ls -lrth *FRAUDASSESSMENTSTATUS_*$filedate*dat.bak | awk '{print " <td> "$8 " </td><td> "$6 " " $7 "</td>"}'`


export tdate=`date "+%Y-%m-%d"`;
cd /opt/app/agilis/bak/out6/

bakcount=`ls -ltr *.dat.bak|grep $tdate|wc -l`

#Number of Records

export edate=`date "+%Y%m%d"`;
cd /opt/app/agilis/data/out/edw_temp
alrec=`wc -l *ALERT*.dat|grep $edate |awk '{print  $1  }'`;
aurec=`wc -l *AUDIT*.dat|grep $edate |awk '{print  $1  }'`;
carec=`wc -l *CASE*.dat|grep $edate |awk '{print  $1  }'`;
edrec=`wc -l *EDLSTATUS*.dat|grep $edate |awk '{print  $1  }'`;
curec=`wc -l *CUST*.dat|grep $edate |awk '{print  $1  }'`;
edlrec=`wc -l *EDLRESP*.dat|grep $edate |awk '{print  $1  }'`;
edlqrec=`wc -l *EDLREQ*.dat|grep $edate |awk '{print  $1  }'`;
pfrec=`wc -l *PFR*.dat|grep $edate |awk '{print  $1  }'`;
derec=`wc -l *DEFAULTER*.dat|grep $edate |awk '{print  $1  }'`;
orrec=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate |awk '{print  $1  }'`;
aprec=`wc -l *APPLICATION_*.dat|grep $edate |awk '{print  $1  }'`;
ordrec=`wc -l *ORDERS_*.dat|grep $edate |awk '{print  $1  }'`;
orderec=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate |awk '{print  $1  }'`;
carrec=`wc -l *CAREQLOG_*.dat|grep $edate |awk '{print  $1  }'`;
alirec=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate |awk '{print  $1  }'`;
frarec=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate |awk '{print  $1  }'`;

export edate1=`date "+%Y%m%d" --date="1 day ago"`;
cd /opt/app/agilis/data/out/edw_temp
alrec1=`wc -l *ALERT*.dat|grep $edate1 |awk '{print  $1  }'`;
aurec1=`wc -l *AUDIT*.dat|grep $edate1 |awk '{print  $1  }'`;
carec1=`wc -l *CASE*.dat|grep $edate1 |awk '{print  $1  }'`;
edrec1=`wc -l *EDLSTATUS*.dat|grep $edate1 |awk '{print  $1  }'`;
curec1=`wc -l *CUST*.dat|grep $edate1 |awk '{print  $1  }'`;
edlrec1=`wc -l *EDLRESP*.dat|grep $edate1 |awk '{print  $1  }'`;
edlqrec1=`wc -l *EDLREQ*.dat|grep $edate1 |awk '{print  $1  }'`;
pfrec1=`wc -l *PFR*.dat|grep $edate1 |awk '{print  $1  }'`;
derec1=`wc -l *DEFAULTER*.dat|grep $edate1 |awk '{print  $1  }'`;
orrec1=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate1 |awk '{print  $1  }'`;
aprec1=`wc -l *APPLICATION_*.dat|grep $edate1 |awk '{print  $1  }'`;
ordrec1=`wc -l *ORDERS_*.dat|grep $edate1 |awk '{print  $1  }'`;
orderec1=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate1 |awk '{print  $1  }'`;
carrec1=`wc -l *CAREQLOG_*.dat|grep $edate1 |awk '{print  $1  }'`;
alirec1=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate1 |awk '{print  $1  }'`;
frarec1=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate1 |awk '{print  $1  }'`;

export edate2=`date "+%Y%m%d" --date="2 day ago"`;
cd /opt/app/agilis/data/out/edw_temp
alrec2=`wc -l *ALERT*.dat|grep $edate2 |awk '{print  $1  }'`;
aurec2=`wc -l *AUDIT*.dat|grep $edate2 |awk '{print  $1  }'`;
carec2=`wc -l *CASE*.dat|grep $edate2 |awk '{print  $1  }'`;
edrec2=`wc -l *EDLSTATUS*.dat|grep $edate2 |awk '{print  $1  }'`;
curec2=`wc -l *CUST*.dat|grep $edate2 |awk '{print  $1  }'`;
edlrec2=`wc -l *EDLRESP*.dat|grep $edate2 |awk '{print  $1  }'`;
edlqrec2=`wc -l *EDLREQ*.dat|grep $edate2 |awk '{print  $1  }'`;
pfrec2=`wc -l *PFR*.dat|grep $edate2 |awk '{print  $1  }'`;
derec2=`wc -l *DEFAULTER*.dat|grep $edate2 |awk '{print  $1  }'`;
orrec2=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate2 |awk '{print  $1  }'`;
aprec2=`wc -l *APPLICATION_*.dat|grep $edate2 |awk '{print  $1  }'`;
ordrec2=`wc -l *ORDERS_*.dat|grep $edate2 |awk '{print  $1  }'`;
orderec2=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate2 |awk '{print  $1  }'`;
carrec2=`wc -l *CAREQLOG_*.dat|grep $edate2 |awk '{print  $1  }'`;
alirec2=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate2 |awk '{print  $1  }'`;
frarec2=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate2 |awk '{print  $1  }'`;

export edate3=`date "+%Y%m%d" --date="3 day ago"`;
cd /opt/app/agilis/data/out/edw_temp
alrec3=`wc -l *ALERT*.dat|grep $edate3 |awk '{print  $1  }'`;
aurec3=`wc -l *AUDIT*.dat|grep $edate3 |awk '{print  $1  }'`;
carec3=`wc -l *CASE*.dat|grep $edate3 |awk '{print  $1  }'`;
edrec3=`wc -l *EDLSTATUS*.dat|grep $edate3 |awk '{print  $1  }'`;
curec3=`wc -l *CUST*.dat|grep $edate3 |awk '{print  $1  }'`;
edlrec3=`wc -l *EDLRESP*.dat|grep $edate3 |awk '{print  $1  }'`;
edlqrec3=`wc -l *EDLREQ*.dat|grep $edate3 |awk '{print  $1  }'`;
pfrec3=`wc -l *PFR*.dat|grep $edate3 |awk '{print  $1  }'`;
derec3=`wc -l *DEFAULTER*.dat|grep $edate3 |awk '{print  $1  }'`;
orrec3=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate3 |awk '{print  $1  }'`;
aprec3=`wc -l *APPLICATION_*.dat|grep $edate3 |awk '{print  $1  }'`;
ordrec3=`wc -l *ORDERS_*.dat|grep $edate3 |awk '{print  $1  }'`;
orderec3=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate3 |awk '{print  $1  }'`;
carrec3=`wc -l *CAREQLOG_*.dat|grep $edate3 |awk '{print  $1  }'`;
alirec3=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate3 |awk '{print  $1  }'`;
frarec3=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate3 |awk '{print  $1  }'`;

export edate4=`date "+%Y%m%d" --date="4 day ago"`;
cd /opt/app/agilis/data/out/edw_temp
alrec4=`wc -l *ALERT*.dat|grep $edate4 |awk '{print  $1  }'`;
aurec4=`wc -l *AUDIT*.dat|grep $edate4 |awk '{print  $1  }'`;
carec4=`wc -l *CASE*.dat|grep $edate4 |awk '{print  $1  }'`;
edrec4=`wc -l *EDLSTATUS*.dat|grep $edate4 |awk '{print  $1  }'`;
curec4=`wc -l *CUST*.dat|grep $edate4 |awk '{print  $1  }'`;
edlrec4=`wc -l *EDLRESP*.dat|grep $edate4 |awk '{print  $1  }'`;
edlqrec4=`wc -l *EDLREQ*.dat|grep $edate4 |awk '{print  $1  }'`;
pfrec4=`wc -l *PFR*.dat|grep $edate4 |awk '{print  $1  }'`;
derec4=`wc -l *DEFAULTER*.dat|grep $edate4 |awk '{print  $1  }'`;
orrec4=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate4 |awk '{print  $1  }'`;
aprec4=`wc -l *APPLICATION_*.dat|grep $edate4 |awk '{print  $1  }'`;
ordrec4=`wc -l *ORDERS_*.dat|grep $edate4 |awk '{print  $1  }'`;
orderec4=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate4 |awk '{print  $1  }'`;
carrec4=`wc -l *CAREQLOG_*.dat|grep $edate4 |awk '{print  $1  }'`;
alirec4=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate4 |awk '{print  $1  }'`;
frarec4=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate4 |awk '{print  $1  }'`;
#new addition
export edate5=`date "+%Y%m%d" --date="5 day ago"`;
alrec5=`wc -l *ALERT*.dat|grep $edate5 |awk '{print  $1  }'`;
aurec5=`wc -l *AUDIT*.dat|grep $edate5 |awk '{print  $1  }'`;
carec5=`wc -l *CASE*.dat|grep $edate5 |awk '{print  $1  }'`;
edrec5=`wc -l *EDLSTATUS*.dat|grep $edate5 |awk '{print  $1  }'`;
curec5=`wc -l *CUST*.dat|grep $edate5 |awk '{print  $1  }'`;
edlrec5=`wc -l *EDLRESP*.dat|grep $edate5 |awk '{print  $1  }'`;
edlqrec5=`wc -l *EDLREQ*.dat|grep $edate5 |awk '{print  $1  }'`;
pfrec5=`wc -l *PFR*.dat|grep $edate5 |awk '{print  $1  }'`;
derec5=`wc -l *DEFAULTER*.dat|grep $edate5 |awk '{print  $1  }'`;
orrec5=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate5 |awk '{print  $1  }'`;
aprec5=`wc -l *APPLICATION_*.dat|grep $edate5 |awk '{print  $1  }'`;
ordrec5=`wc -l *ORDERS_*.dat|grep $edate5 |awk '{print  $1  }'`;
orderec5=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate5 |awk '{print  $1  }'`;
carrec5=`wc -l *CAREQLOG_*.dat|grep $edate5 |awk '{print  $1  }'`;
alirec5=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate5 |awk '{print  $1  }'`;
frarec5=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate5 |awk '{print  $1  }'`;

export edate6=`date "+%Y%m%d" --date="6 day ago"`;
alrec6=`wc -l *ALERT*.dat|grep $edate6 |awk '{print  $1  }'`;
aurec6=`wc -l *AUDIT*.dat|grep $edate6 |awk '{print  $1  }'`;
carec6=`wc -l *CASE*.dat|grep $edate6 |awk '{print  $1  }'`;
edrec6=`wc -l *EDLSTATUS*.dat|grep $edate6 |awk '{print  $1  }'`;
curec6=`wc -l *CUST*.dat|grep $edate6 |awk '{print  $1  }'`;
edlrec6=`wc -l *EDLRESP*.dat|grep $edate6 |awk '{print  $1  }'`;
edlqrec6=`wc -l *EDLREQ*.dat|grep $edate6 |awk '{print  $1  }'`;
pfrec6=`wc -l *PFR*.dat|grep $edate6 |awk '{print  $1  }'`;
derec6=`wc -l *DEFAULTER*.dat|grep $edate6 |awk '{print  $1  }'`;
orrec6=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate6 |awk '{print  $1  }'`;
aprec6=`wc -l *APPLICATION_*.dat|grep $edate6 |awk '{print  $1  }'`;
ordrec6=`wc -l *ORDERS_*.dat|grep $edate6 |awk '{print  $1  }'`;
orderec6=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate6 |awk '{print  $1  }'`;
carrec6=`wc -l *CAREQLOG_*.dat|grep $edate6 |awk '{print  $1  }'`;
alirec6=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate6 |awk '{print  $1  }'`;
frarec6=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate6 |awk '{print  $1  }'`;

export edate7=`date "+%Y%m%d" --date="7 day ago"`;
alrec7=`wc -l *ALERT*.dat|grep $edate7|awk '{print  $1  }'`;
aurec7=`wc -l *AUDIT*.dat|grep $edate7|awk '{print  $1  }'`;
carec7=`wc -l *CASE*.dat|grep $edate7|awk '{print  $1  }'`;
edrec7=`wc -l *EDLSTATUS*.dat|grep $edate7|awk '{print  $1  }'`;
curec7=`wc -l *CUST*.dat|grep $edate7|awk '{print  $1  }'`;
edlrec7=`wc -l *EDLRESP*.dat|grep $edate7|awk '{print  $1  }'`;
edlqrec7=`wc -l *EDLREQ*.dat|grep $edate7|awk '{print  $1  }'`;
pfrec7=`wc -l *PFR*.dat|grep $edate7|awk '{print  $1  }'`;
derec7=`wc -l *DEFAULTER*.dat|grep $edate7|awk '{print  $1  }'`;
orrec7=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate7|awk '{print  $1  }'`;
aprec7=`wc -l *APPLICATION_*.dat|grep $edate7|awk '{print  $1  }'`;
ordrec7=`wc -l *ORDERS_*.dat|grep $edate7|awk '{print  $1  }'`;
orderec7=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate7|awk '{print  $1  }'`;
carrec7=`wc -l *CAREQLOG_*.dat|grep $edate7|awk '{print  $1  }'`;
alirec7=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate7|awk '{print  $1  }'`;
frarec7=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate7 |awk '{print  $1  }'`;

export edate8=`date "+%Y%m%d" --date="8 day ago"`;
alrec8=`wc -l *ALERT*.dat|grep $edate8|awk '{print  $1  }'`;
aurec8=`wc -l *AUDIT*.dat|grep $edate8|awk '{print  $1  }'`;
carec8=`wc -l *CASE*.dat|grep $edate8|awk '{print  $1  }'`;
edrec8=`wc -l *EDLSTATUS*.dat|grep $edate8|awk '{print  $1  }'`;
curec8=`wc -l *CUST*.dat|grep $edate8|awk '{print  $1  }'`;
edlrec8=`wc -l *EDLRESP*.dat|grep $edate8|awk '{print  $1  }'`;
edlqrec8=`wc -l *EDLREQ*.dat|grep $edate8|awk '{print  $1  }'`;
pfrec8=`wc -l *PFR*.dat|grep $edate8|awk '{print  $1  }'`;
derec8=`wc -l *DEFAULTER*.dat|grep $edate8|awk '{print  $1  }'`;
orrec8=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate8|awk '{print  $1  }'`;
aprec8=`wc -l *APPLICATION_*.dat|grep $edate8|awk '{print  $1  }'`;
ordrec8=`wc -l *ORDERS_*.dat|grep $edate8|awk '{print  $1  }'`;
orderec8=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate8|awk '{print  $1  }'`;
carrec8=`wc -l *CAREQLOG_*.dat|grep $edate8|awk '{print  $1  }'`;
alirec8=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate8|awk '{print  $1  }'`;
frarec8=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate8 |awk '{print  $1  }'`;

export edate9=`date "+%Y%m%d" --date="9 day ago"`;
alrec9=`wc -l *ALERT*.dat|grep $edate9|awk '{print  $1  }'`;
aurec9=`wc -l *AUDIT*.dat|grep $edate9|awk '{print  $1  }'`;
carec9=`wc -l *CASE*.dat|grep $edate9|awk '{print  $1  }'`;
edrec9=`wc -l *EDLSTATUS*.dat|grep $edate9|awk '{print  $1  }'`;
curec9=`wc -l *CUST*.dat|grep $edate9|awk '{print  $1  }'`;
edlrec9=`wc -l *EDLRESP*.dat|grep $edate9|awk '{print  $1  }'`;
edlqrec9=`wc -l *EDLREQ*.dat|grep $edate9|awk '{print  $1  }'`;
pfrec9=`wc -l *PFR*.dat|grep $edate9|awk '{print  $1  }'`;
derec9=`wc -l *DEFAULTER*.dat|grep $edate9|awk '{print  $1  }'`;
orrec9=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate9|awk '{print  $1  }'`;
aprec9=`wc -l *APPLICATION_*.dat|grep $edate9|awk '{print  $1  }'`;
ordrec9=`wc -l *ORDERS_*.dat|grep $edate9|awk '{print  $1  }'`;
orderec9=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate9|awk '{print  $1  }'`;
carrec9=`wc -l *CAREQLOG_*.dat|grep $edate9|awk '{print  $1  }'`;
alirec9=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate9|awk '{print  $1  }'`;
frarec9=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate9 |awk '{print  $1  }'`;



export edate=`date "+%d/%m"`;
export edate1=`date "+%d/%m" --date="1 day ago"`;
export edate2=`date "+%d/%m" --date="2 day ago"`;
export edate3=`date "+%d/%m" --date="3 day ago"`;
export edate4=`date "+%d/%m" --date="4 day ago"`;
export edate5=`date "+%d/%m" --date="5 day ago"`;
export edate6=`date "+%d/%m" --date="6 day ago"`;
export edate7=`date "+%d/%m" --date="7 day ago"`;
export edate8=`date "+%d/%m" --date="8 day ago"`;
export edate9=`date "+%d/%m" --date="9 day ago"`;

echo "<html><title>EDW Report Produced at $reportdate</title>"
echo "<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\" ></script>"
echo "<script type=\"text/javascript\" >"

echo "google.load( \"visualization\", \"1\" , {packages:[\"corechart\"]});"
echo "google.setOnLoadCallback(drawChart);"
echo "function drawChart() "
echo "{"
echo ""
echo "    var data = google.visualization.arrayToDataTable(["
echo "['Date', 'Alert'],"
echo "['$edate',  $alrec ],"
echo "['$edate1',  $alrec1 ],"
echo "['$edate2',  $alrec2 ],"
echo "['$edate3',  $alrec3 ],"
echo "['$edate4',  $alrec4 ],"
echo "['$edate5',  $alrec5 ],"
echo "['$edate6',  $alrec6 ],"
echo "['$edate7',  $alrec7 ],"
echo "['$edate8',  $alrec8 ],"
echo "['$edate9',  $alrec9 ]"
echo "]);"
echo ""
echo "    var options = {"
echo "title: 'Alert Data',"
echo "          "
echo "};"
echo ""
echo "          var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));"
echo ""
echo "          chart.draw(data, options);"
echo ""
echo "}"
echo "  "

echo "google.load( \"visualization\", \"2\", {packages:[\"corechart\"]});"
echo "google.setOnLoadCallback(drawChart2);"
echo "function drawChart2() "
echo "{"
echo ""
echo "    var data = google.visualization.arrayToDataTable(["
echo "['Date', 'Audit'],"
echo "['$edate',  $aurec ],"
echo "['$edate1',  $aurec1 ],"
echo "['$edate2',  $aurec2 ],"
echo "['$edate3',  $aurec3 ],"
echo "['$edate4',  $aurec4 ],"
echo "['$edate5',  $aurec5 ],"
echo "['$edate6',  $aurec6 ],"
echo "['$edate7',  $aurec7 ],"
echo "['$edate8',  $aurec8 ],"
echo "['$edate9',  $aurec9 ]"
echo "]);"
echo ""
echo "    var options = {"
echo "title: 'Audit Data',"
echo "};"
echo ""
echo "          var chart = new google.visualization.ColumnChart(document.getElementById('chart_div2'));"
echo "chart.draw(data, options);"
echo "}"

echo "google.load( \"visualization\", \"3\", {packages:[\"corechart\"]});"
echo "google.setOnLoadCallback(drawChart3);"
echo "function drawChart3() "
echo "{"
echo ""
echo "    var data = google.visualization.arrayToDataTable(["
echo "['Date', 'Case'],"
echo "['$edate',  $carec ],"
echo "['$edate1',  $carec1 ],"
echo "['$edate2',  $carec2 ],"
echo "['$edate3',  $carec3 ],"
echo "['$edate4',  $carec4 ],"
echo "['$edate5',  $carec5 ],"
echo "['$edate6',  $carec6 ],"
echo "['$edate7',  $carec7 ],"
echo "['$edate8',  $carec8 ],"
echo "['$edate9',  $carec9 ]"
echo "]);"
echo ""
echo "    var options = {"
echo "title: 'Case Data',"
echo "};"
echo ""
echo "          var chart = new google.visualization.ColumnChart(document.getElementById('chart_div3'));"
echo "chart.draw(data, options);"
echo "}"

echo "google.load( \"visualization\", \"4\", {packages:[\"corechart\"]});"
echo "google.setOnLoadCallback(drawChart4);"
echo "function drawChart4() "
echo "{"
echo ""
echo "    var data = google.visualization.arrayToDataTable(["
echo "['Date', 'EDL Status'],"
echo "['$edate',  $edrec ],"
echo "['$edate1',  $edrec1 ],"
echo "['$edate2',  $edrec2 ],"
echo "['$edate3',  $edrec3 ],"
echo "['$edate4',  $edrec4 ],"
echo "['$edate5',  $edrec5 ],"
echo "['$edate6',  $edrec6 ],"
echo "['$edate7',  $edrec7 ],"
echo "['$edate8',  $edrec8 ],"
echo "['$edate9',  $edrec9 ]"
echo "]);"
echo ""
echo "    var options = {"
echo "title: 'EDL Status Data',"
echo "};"
echo ""
echo "          var chart = new google.visualization.ColumnChart(document.getElementById('chart_div4'));"
echo "chart.draw(data, options);"
echo "}"

echo "google.load( \"visualization\", \"5\", {packages:[\"corechart\"]});"
echo "google.setOnLoadCallback(drawChart5);"
echo "function drawChart5() "
echo "{"
echo ""
echo "    var data = google.visualization.arrayToDataTable(["
echo "['Date', 'CUST File'],"
echo "['$edate',  $curec ],"
echo "['$edate1',  $curec1 ],"
echo "['$edate2',  $curec2 ],"
echo "['$edate3',  $curec3 ],"
echo "['$edate4',  $curec4 ],"
echo "['$edate5',  $curec5 ],"
echo "['$edate6',  $curec6 ],"
echo "['$edate7',  $curec7 ],"
echo "['$edate8',  $curec8 ],"
echo "['$edate9',  $curec9 ]"
echo "]);"
echo ""
echo "    var options = {"
echo "title: 'CUST Data',"
echo "};"
echo ""
echo "          var chart = new google.visualization.ColumnChart(document.getElementById('chart_div5'));"
echo "chart.draw(data, options);"
echo "}"

#6 EDL Response
echo "google.load( \"visualization\", \"6\", {packages:[\"corechart\"]});"
echo "google.setOnLoadCallback(drawChart6);"
echo "function drawChart6() "
echo "{"
echo ""
echo "    var data = google.visualization.arrayToDataTable(["
echo "['Date', 'EDL Response'],"
echo "['$edate',  $edlrec ],"
echo "['$edate1',  $edlrec1 ],"
echo "['$edate2',  $edlrec2 ],"
echo "['$edate3',  $edlrec3 ],"
echo "['$edate4',  $edlrec4 ],"
echo "['$edate5',  $edlrec5 ],"
echo "['$edate6',  $edlrec6 ],"
echo "['$edate7',  $edlrec7 ],"
echo "['$edate8',  $edlrec8 ],"
echo "['$edate9',  $edlrec9 ]"
echo "]);"
echo ""
echo "    var options = {"
echo "title: 'EDL Response',"
echo "};"
echo ""
echo "          var chart = new google.visualization.ColumnChart(document.getElementById('chart_div6'));"
echo "chart.draw(data, options);"
echo "}"

#7 EDL Request
echo "google.load( \"visualization\", \"7\", {packages:[\"corechart\"]});"
echo "google.setOnLoadCallback(drawChart7);"
echo "function drawChart7() "
echo "{"
echo ""
echo "    var data = google.visualization.arrayToDataTable(["
echo "['Date', 'EDl Request'],"
echo "['$edate',  $edlqrec ],"
echo "['$edate1',  $edlqrec1 ],"
echo "['$edate2',  $edlqrec2 ],"
echo "['$edate3',  $edlqrec3 ],"
echo "['$edate4',  $edlqrec4 ],"
echo "['$edate5',  $edlqrec5 ],"
echo "['$edate6',  $edlqrec6 ],"
echo "['$edate7',  $edlqrec7 ],"
echo "['$edate8',  $edlqrec8 ],"
echo "['$edate9',  $edlqrec9 ]"
echo "]);"
echo ""
echo "    var options = {"
echo "title: 'EDL Request Data',"
echo "};"
echo ""
echo "          var chart = new google.visualization.ColumnChart(document.getElementById('chart_div7'));"
echo "chart.draw(data, options);"
echo "}"

#8 PFR
echo "google.load( \"visualization\", \"8\", {packages:[\"corechart\"]});"
echo "google.setOnLoadCallback(drawChart8);"
echo "function drawChart8() "
echo "{"
echo ""
echo "    var data = google.visualization.arrayToDataTable(["
echo "['Date', 'PFR File'],"
echo "['$edate',  $pfrec ],"
echo "['$edate1',  $pfrec1 ],"
echo "['$edate2',  $pfrec2 ],"
echo "['$edate3',  $pfrec3 ],"
echo "['$edate4',  $pfrec4 ],"
echo "['$edate5',  $pfrec5 ],"
echo "['$edate6',  $pfrec6 ],"
echo "['$edate7',  $pfrec7 ],"
echo "['$edate8',  $pfrec8 ],"
echo "['$edate9',  $pfrec9 ]"
echo "]);"
echo ""
echo "    var options = {"
echo "title: 'PFR Data',"
echo "};"
echo ""
echo "          var chart = new google.visualization.ColumnChart(document.getElementById('chart_div8'));"
echo "chart.draw(data, options);"
echo "}"

#9 Defaulter
echo "google.load( \"visualization\", \"9\", {packages:[\"corechart\"]});"
echo "google.setOnLoadCallback(drawChart9);"
echo "function drawChart9() "
echo "{"
echo ""
echo "    var data = google.visualization.arrayToDataTable(["
echo "['Date', 'Defaulter'],"
echo "['$edate',  $derec ],"
echo "['$edate1',  $derec1 ],"
echo "['$edate2',  $derec2 ],"
echo "['$edate3',  $derec3 ],"
echo "['$edate4',  $derec4 ],"
echo "['$edate5',  $derec5 ],"
echo "['$edate6',  $derec6 ],"
echo "['$edate7',  $derec7 ],"
echo "['$edate8',  $derec8 ],"
echo "['$edate9',  $derec9 ]"
echo "]);"
echo ""
echo "    var options = {"
echo "title: 'Defaulter',"
echo "};"
echo ""
echo "          var chart = new google.visualization.ColumnChart(document.getElementById('chart_div9'));"
echo "chart.draw(data, options);"
echo "}"

#10 OCA
echo "google.load( \"visualization\", \"10\", {packages:[\"corechart\"]});"
echo "google.setOnLoadCallback(drawChart10);"
echo "function drawChart10() "
echo "{"
echo ""
echo "    var data = google.visualization.arrayToDataTable(["
echo "['Date', 'OCA'],"
echo "['$edate',  $orrec ],"
echo "['$edate1',  $orrec1 ],"
echo "['$edate2',  $orrec2 ],"
echo "['$edate3',  $orrec3 ],"
echo "['$edate4',  $orrec4 ],"
echo "['$edate5',  $orrec5 ],"
echo "['$edate6',  $orrec6 ],"
echo "['$edate7',  $orrec7 ],"
echo "['$edate8',  $orrec8 ],"
echo "['$edate9',  $orrec9 ]"
echo "]);"
echo ""
echo "    var options = {"
echo "title: 'OCA',"
echo "};"
echo ""
echo "          var chart = new google.visualization.ColumnChart(document.getElementById('chart_div10'));"
echo "chart.draw(data, options);"
echo "}"

#11 Application
echo "google.load( \"visualization\", \"11\", {packages:[\"corechart\"]});"
echo "google.setOnLoadCallback(drawChart11);"
echo "function drawChart11() "
echo "{"
echo ""
echo "    var data = google.visualization.arrayToDataTable(["
echo "['Date', 'Application'],"
echo "['$edate',  $aprec ],"
echo "['$edate1',  $aprec1 ],"
echo "['$edate2',  $aprec2 ],"
echo "['$edate3',  $aprec3 ],"
echo "['$edate4',  $aprec4 ],"
echo "['$edate5',  $aprec5 ],"
echo "['$edate6',  $aprec6 ],"
echo "['$edate7',  $aprec7 ],"
echo "['$edate8',  $aprec8 ],"
echo "['$edate9',  $aprec9 ]"
echo "]);"
echo ""
echo "    var options = {"
echo "title: 'Application',"
echo "};"
echo ""
echo "          var chart = new google.visualization.ColumnChart(document.getElementById('chart_div11'));"
echo "chart.draw(data, options);"
echo "}"

#12 Orders
echo "google.load( \"visualization\", \"12\", {packages:[\"corechart\"]});"
echo "google.setOnLoadCallback(drawChart12);"
echo "function drawChart12() "
echo "{"
echo ""
echo "    var data = google.visualization.arrayToDataTable(["
echo "['Date', 'Orders'],"
echo "['$edate',  $ordrec ],"
echo "['$edate1',  $ordrec1 ],"
echo "['$edate2',  $ordrec2 ],"
echo "['$edate3',  $ordrec3 ],"
echo "['$edate4',  $ordrec4 ],"
echo "['$edate5',  $ordrec5 ],"
echo "['$edate6',  $ordrec6 ],"
echo "['$edate7',  $ordrec7 ],"
echo "['$edate8',  $ordrec8 ],"
echo "['$edate9',  $ordrec9 ]"
echo "]);"
echo ""
echo "    var options = {"
echo "title: 'Orders',"
echo "};"
echo ""
echo "          var chart = new google.visualization.ColumnChart(document.getElementById('chart_div12'));"
echo "chart.draw(data, options);"
echo "}"

#13 Order Delivery Status
echo "google.load( \"visualization\", \"13\", {packages:[\"corechart\"]});"
echo "google.setOnLoadCallback(drawChart13);"
echo "function drawChart13() "
echo "{"
echo ""
echo "    var data = google.visualization.arrayToDataTable(["
echo "['Date', 'Order Delivery'],"
echo "['$edate',  $orderec ],"
echo "['$edate1',  $orderec1 ],"
echo "['$edate2',  $orderec2 ],"
echo "['$edate3',  $orderec3 ],"
echo "['$edate4',  $orderec4 ],"
echo "['$edate5',  $orderec5 ],"
echo "['$edate6',  $orderec6 ],"
echo "['$edate7',  $orderec7 ],"
echo "['$edate8',  $orderec8 ],"
echo "['$edate9',  $orderec9 ]"
echo "]);"
echo ""
echo "    var options = {"
echo "title: 'Order Delivery',"
echo "};"
echo ""
echo "          var chart = new google.visualization.ColumnChart(document.getElementById('chart_div13'));"
echo "chart.draw(data, options);"
echo "}"

#14 CAREQLog
echo "google.load( \"visualization\", \"14\", {packages:[\"corechart\"]});"
echo "google.setOnLoadCallback(drawChart14);"
echo "function drawChart14() "
echo "{"
echo ""
echo "    var data = google.visualization.arrayToDataTable(["
echo "['Date', 'CAREQLog'],"
echo "['$edate',  $carrec ],"
echo "['$edate1',  $carrec1 ],"
echo "['$edate2',  $carrec2 ],"
echo "['$edate3',  $carrec3 ],"
echo "['$edate4',  $carrec4 ],"
echo "['$edate5',  $carrec5 ],"
echo "['$edate6',  $carrec6 ],"
echo "['$edate7',  $carrec7 ],"
echo "['$edate8',  $carrec8 ],"
echo "['$edate9',  $carrec9 ]"
echo "]);"
echo ""
echo "    var options = {"
echo "title: 'CAREQLog',"
echo "};"
echo ""
echo "          var chart = new google.visualization.ColumnChart(document.getElementById('chart_div14'));"
echo "chart.draw(data, options);"
echo "}"

#15 Aliasing Match Details
echo "google.load( \"visualization\", \"15\", {packages:[\"corechart\"]});"
echo "google.setOnLoadCallback(drawChart15);"
echo "function drawChart15() "
echo "{"
echo ""
echo "    var data = google.visualization.arrayToDataTable(["
echo "['Date', 'AliasingMatch'],"
echo "['$edate',  $alirec ],"
echo "['$edate1',  $alirec1 ],"
echo "['$edate2',  $alirec2 ],"
echo "['$edate3',  $alirec3 ],"
echo "['$edate4',  $alirec4 ],"
echo "['$edate5',  $alirec5 ],"
echo "['$edate6',  $alirec6 ],"
echo "['$edate7',  $alirec7 ],"
echo "['$edate8',  $alirec8 ],"
echo "['$edate9',  $alirec9 ]"
echo "]);"
echo ""
echo "    var options = {"
echo "title: 'AliasingMatch',"
echo "};"
echo ""
echo "          var chart = new google.visualization.ColumnChart(document.getElementById('chart_div15'));"
echo "chart.draw(data, options);"
echo "}"

#16 FRAUD ASSESSMENT STATUS
echo "google.load( \"visualization\", \"16\", {packages:[\"corechart\"]});"
echo "google.setOnLoadCallback(drawChart16);"
echo "function drawChart16() "
echo "{"
echo ""
echo "    var data = google.visualization.arrayToDataTable(["
echo "['Date', 'Fraud'],"
echo "['$edate',  $frarec ],"
echo "['$edate1',  $frarec1 ],"
echo "['$edate2',  $frarec2 ],"
echo "['$edate3',  $frarec3 ],"
echo "['$edate4',  $frarec4 ],"
echo "['$edate5',  $frarec5 ],"
echo "['$edate6',  $frarec6 ],"
echo "['$edate7',  $frarec7 ],"
echo "['$edate8',  $frarec8 ],"
echo "['$edate9',  $frarec9 ]"
echo "]);"
echo ""
echo "    var options = {"
echo "title: 'FRAUDASSESSMENT STATUS',"
echo "};"
echo ""
echo "          var chart = new google.visualization.ColumnChart(document.getElementById('chart_div16'));"
echo "chart.draw(data, options);"
echo "}"

echo "</script>"

#Report Style

echo "<style>"
echo "*{font-family:arial;}"
echo "table{margin:20px 0;border:0;border-collapse:collapse;}"
echo "table tr td{font-family:arial;font-size:12px;padding:4px 6px;border:#333 solid 1px;}"
echo "table tr th{font-family:arial;font-size:14px;padding:8px 6px;font-weight:bold;text-align:center;background:#efefef;border:#333 solid 1px;}"
echo "</style>"

#Report Body

echo "<body>"
echo "<b>EDW Report : $reportdate</b>"
echo "<table border = 1>"
echo "<tr><td>File Name</td><td>File Generated</td><td>Created at</td><td>File Size</td><td>File sent to EDW</td><td>Sent Time</td></tr>"
echo "<tr>"$alert " " $salert "</tr>"
echo "<tr>"$audit " " $saudit"</tr>"
echo "<tr>"$case " " $scase"</tr>"
echo "<tr>"$edlstatus " " $sedlstatus"</tr>"
echo "<tr>"$cust " " $scust"</tr>"
echo "<tr>"$edlresp " "$sedlresp"</tr>"
echo "<tr>"$edlreq " " $sedlreq"</tr>"
echo "<tr>"$pfr " " $spfr"</tr>"
echo "<tr>"$defaulter " " $sdefaulter"</tr>"
echo "<tr>"$oca " " $soca"</tr>"
echo "<tr>"$app " " $sapp"</tr>"
echo "<tr>"$orders " " $sorders"</tr>"
echo "<tr>"$oderdev " " $soderdev"</tr>"
echo "<tr>"$careqlog " " $scareqlog"</tr>"
echo "<tr>"$aliasmatch " " $saliasmatch"</tr>"
echo "<tr>"$fraud " " $sfraud"</tr>"

echo "</table>"
echo "<table border = 1>"
echo "<tr><td>count in out6 Directory " $datacount" </td></tr>"
echo "<tr><td>count in bak/out6 Directory "$bakcount" </td></tr>"
echo "</table>"


#Google Visualization Code
echo "<table border = 1>"

echo "<tr><td>"
echo    "<div id= \"chart_div\" style=\" width: 400px; height: 400px;\" ></div>"
echo "</td><td>"
echo    "<div id= \"chart_div2\" style=\"width: 400px; height: 400px;\" ></div>"
echo "</td></tr>"

echo "<tr><td>"
echo    "<div id= \"chart_div3\" style=\" width: 400px; height: 400px;\" ></div>"
echo "</td><td>"
echo    "<div id= \"chart_div4\" style=\"width: 400px; height: 400px;\" ></div>"
echo "</td></tr>"

echo "<tr><td>"
echo    "<div id= \"chart_div5\" style=\" width: 400px; height: 400px;\" ></div>"
echo "</td><td>"
echo    "<div id= \"chart_div6\" style=\"width: 400px; height: 400px;\" ></div>"
echo "</td></tr>"

echo "<tr><td>"
echo    "<div id= \"chart_div7\" style=\" width: 400px; height: 400px;\" ></div>"
echo "</td><td>"
echo    "<div id= \"chart_div8\" style=\"width: 400px; height: 400px;\" ></div>"
echo "</td></tr>"

echo "<tr><td>"
echo    "<div id= \"chart_div9\" style=\" width: 400px; height: 400px;\" ></div>"
echo "</td><td>"
echo    "<div id= \"chart_div10\" style=\"width: 400px; height: 400px;\" ></div>"
echo "</td></tr>"

echo "<tr><td>"
echo    "<div id= \"chart_div11\" style=\" width: 400px; height: 400px;\" ></div>"
echo "</td><td>"
echo    "<div id= \"chart_div12\" style=\"width: 400px; height: 400px;\" ></div>"
echo "</td></tr>"

echo "<tr><td>"
echo    "<div id= \"chart_div13\" style=\" width: 400px; height: 400px;\" ></div>"
echo "</td><td>"
echo    "<div id= \"chart_div14\" style=\"width: 400px; height: 400px;\" ></div>"
echo "</td></tr>"

echo "<tr><td>"
echo    "<div id= \"chart_div15\" style=\" width: 400px; height: 400px;\" ></div>"
echo "</td><td>"
echo    "<div id= \"chart_div16\" style=\"width: 400px; height: 400px;\" ></div>"
echo "</td></tr>"

echo "</table>"

#Number of Records

export edate=`date "+%Y%m%d"`;
cd /opt/app/agilis/data/out/edw_temp
alrec=`wc -l *ALERT*.dat|grep $edate |awk '{print " <td> "$1 " </td>"}'`;
aurec=`wc -l *AUDIT*.dat|grep $edate |awk '{print " <td> "$1 " </td>"}'`;
carec=`wc -l *CASE*.dat|grep $edate |awk '{print " <td> "$1 " </td>"}'`;
edrec=`wc -l *EDLSTATUS*.dat|grep $edate |awk '{print " <td> "$1 " </td>"}'`;
curec=`wc -l *CUST*.dat|grep $edate |awk '{print " <td> "$1 " </td>"}'`;
edlrec=`wc -l *EDLRESP*.dat|grep $edate |awk '{print " <td> "$1 " </td>"}'`;
edlqrec=`wc -l *EDLREQ*.dat|grep $edate |awk '{print " <td> "$1 " </td>"}'`;
pfrec=`wc -l *PFR*.dat|grep $edate |awk '{print " <td> "$1 " </td>"}'`;
derec=`wc -l *DEFAULTER*.dat|grep $edate |awk '{print " <td> "$1 " </td>"}'`;
orrec=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate |awk '{print " <td> "$1 " </td>"}'`;
aprec=`wc -l *APPLICATION_*.dat|grep $edate |awk '{print " <td> "$1 " </td>"}'`;
ordrec=`wc -l *ORDERS_*.dat|grep $edate |awk '{print " <td> "$1 " </td>"}'`;
orderec=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate |awk '{print " <td> "$1 " </td>"}'`;
carrec=`wc -l *CAREQLOG_*.dat|grep $edate |awk '{print " <td> "$1 " </td>"}'`;
alirec=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate |awk '{print " <td> "$1 " </td>"}'`;
frarec=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate |awk '{print " <td> "$1 " </td>"}'`;

export edate1=`date "+%Y%m%d" --date="1 day ago"`;
cd /opt/app/agilis/data/out/edw_temp
alrec1=`wc -l *ALERT*.dat|grep $edate1 |awk '{print " <td> "$1 " </td>"}'`;
aurec1=`wc -l *AUDIT*.dat|grep $edate1 |awk '{print " <td> "$1 " </td>"}'`;
carec1=`wc -l *CASE*.dat|grep $edate1 |awk '{print " <td> "$1 " </td>"}'`;
edrec1=`wc -l *EDLSTATUS*.dat|grep $edate1 |awk '{print " <td> "$1 " </td>"}'`;
curec1=`wc -l *CUST*.dat|grep $edate1 |awk '{print " <td> "$1 " </td>"}'`;
edlrec1=`wc -l *EDLRESP*.dat|grep $edate1 |awk '{print " <td> "$1 " </td>"}'`;
edlqrec1=`wc -l *EDLREQ*.dat|grep $edate1 |awk '{print " <td> "$1 " </td>"}'`;
pfrec1=`wc -l *PFR*.dat|grep $edate1 |awk '{print " <td> "$1 " </td>"}'`;
derec1=`wc -l *DEFAULTER*.dat|grep $edate1 |awk '{print " <td> "$1 " </td>"}'`;
orrec1=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate1 |awk '{print " <td> "$1 " </td>"}'`;
aprec1=`wc -l *APPLICATION_*.dat|grep $edate1 |awk '{print " <td> "$1 " </td>"}'`;
ordrec1=`wc -l *ORDERS_*.dat|grep $edate1 |awk '{print " <td> "$1 " </td>"}'`;
orderec1=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate1 |awk '{print " <td> "$1 " </td>"}'`;
carrec1=`wc -l *CAREQLOG_*.dat|grep $edate1 |awk '{print " <td> "$1 " </td>"}'`;
alirec1=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate1 |awk '{print " <td> "$1 " </td>"}'`;
frarec1=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate1 |awk '{print " <td> "$1 " </td>"}'`;


export edate2=`date "+%Y%m%d" --date="2 day ago"`;
cd /opt/app/agilis/data/out/edw_temp
alrec2=`wc -l *ALERT*.dat|grep $edate2 |awk '{print " <td> "$1 " </td>"}'`;
aurec2=`wc -l *AUDIT*.dat|grep $edate2 |awk '{print " <td> "$1 " </td>"}'`;
carec2=`wc -l *CASE*.dat|grep $edate2 |awk '{print " <td> "$1 " </td>"}'`;
edrec2=`wc -l *EDLSTATUS*.dat|grep $edate2 |awk '{print " <td> "$1 " </td>"}'`;
curec2=`wc -l *CUST*.dat|grep $edate2 |awk '{print " <td> "$1 " </td>"}'`;
edlrec2=`wc -l *EDLRESP*.dat|grep $edate2 |awk '{print " <td> "$1 " </td>"}'`;
edlqrec2=`wc -l *EDLREQ*.dat|grep $edate2 |awk '{print " <td> "$1 " </td>"}'`;
pfrec2=`wc -l *PFR*.dat|grep $edate2 |awk '{print " <td> "$1 " </td>"}'`;
derec2=`wc -l *DEFAULTER*.dat|grep $edate2 |awk '{print " <td> "$1 " </td>"}'`;
orrec2=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate2 |awk '{print " <td> "$1 " </td>"}'`;
aprec2=`wc -l *APPLICATION_*.dat|grep $edate2 |awk '{print " <td> "$1 " </td>"}'`;
ordrec2=`wc -l *ORDERS_*.dat|grep $edate2 |awk '{print " <td> "$1 " </td>"}'`;
orderec2=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate2 |awk '{print " <td> "$1 " </td>"}'`;
carrec2=`wc -l *CAREQLOG_*.dat|grep $edate2 |awk '{print " <td> "$1 " </td>"}'`;
alirec2=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate2 |awk '{print " <td> "$1 " </td>"}'`;
frarec2=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate2 |awk '{print " <td> "$1 " </td>"}'`;

export edate3=`date "+%Y%m%d" --date="3 day ago"`;
cd /opt/app/agilis/data/out/edw_temp
alrec3=`wc -l *ALERT*.dat|grep $edate3 |awk '{print " <td> "$1 " </td>"}'`;
aurec3=`wc -l *AUDIT*.dat|grep $edate3 |awk '{print " <td> "$1 " </td>"}'`;
carec3=`wc -l *CASE*.dat|grep $edate3 |awk '{print " <td> "$1 " </td>"}'`;
edrec3=`wc -l *EDLSTATUS*.dat|grep $edate3 |awk '{print " <td> "$1 " </td>"}'`;
curec3=`wc -l *CUST*.dat|grep $edate3 |awk '{print " <td> "$1 " </td>"}'`;
edlrec3=`wc -l *EDLRESP*.dat|grep $edate3 |awk '{print " <td> "$1 " </td>"}'`;
edlqrec3=`wc -l *EDLREQ*.dat|grep $edate3 |awk '{print " <td> "$1 " </td>"}'`;
pfrec3=`wc -l *PFR*.dat|grep $edate3 |awk '{print " <td> "$1 " </td>"}'`;
derec3=`wc -l *DEFAULTER*.dat|grep $edate3 |awk '{print " <td> "$1 " </td>"}'`;
orrec3=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate3 |awk '{print " <td> "$1 " </td>"}'`;
aprec3=`wc -l *APPLICATION_*.dat|grep $edate3 |awk '{print " <td> "$1 " </td>"}'`;
ordrec3=`wc -l *ORDERS_*.dat|grep $edate3 |awk '{print " <td> "$1 " </td>"}'`;
orderec3=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate3 |awk '{print " <td> "$1 " </td>"}'`;
carrec3=`wc -l *CAREQLOG_*.dat|grep $edate3 |awk '{print " <td> "$1 " </td>"}'`;
alirec3=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate3 |awk '{print " <td> "$1 " </td>"}'`;
frarec3=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate3 |awk '{print " <td> "$1 " </td>"}'`;

export edate4=`date "+%Y%m%d" --date="4 day ago"`;
cd /opt/app/agilis/data/out/edw_temp
alrec4=`wc -l *ALERT*.dat|grep $edate4 |awk '{print " <td> "$1 " </td>"}'`;
aurec4=`wc -l *AUDIT*.dat|grep $edate4 |awk '{print " <td> "$1 " </td>"}'`;
carec4=`wc -l *CASE*.dat|grep $edate4 |awk '{print " <td> "$1 " </td>"}'`;
edrec4=`wc -l *EDLSTATUS*.dat|grep $edate4 |awk '{print " <td> "$1 " </td>"}'`;
curec4=`wc -l *CUST*.dat|grep $edate4 |awk '{print " <td> "$1 " </td>"}'`;
edlrec4=`wc -l *EDLRESP*.dat|grep $edate4 |awk '{print " <td> "$1 " </td>"}'`;
edlqrec4=`wc -l *EDLREQ*.dat|grep $edate4 |awk '{print " <td> "$1 " </td>"}'`;
pfrec4=`wc -l *PFR*.dat|grep $edate4 |awk '{print " <td> "$1 " </td>"}'`;
derec4=`wc -l *DEFAULTER*.dat|grep $edate4 |awk '{print " <td> "$1 " </td>"}'`;
orrec4=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate4 |awk '{print " <td> "$1 " </td>"}'`;
aprec4=`wc -l *APPLICATION_*.dat|grep $edate4 |awk '{print " <td> "$1 " </td>"}'`;
ordrec4=`wc -l *ORDERS_*.dat|grep $edate4 |awk '{print " <td> "$1 " </td>"}'`;
orderec4=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate4 |awk '{print " <td> "$1 " </td>"}'`;
carrec4=`wc -l *CAREQLOG_*.dat|grep $edate4 |awk '{print " <td> "$1 " </td>"}'`;
alirec4=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate4 |awk '{print " <td> "$1 " </td>"}'`;
frarec4=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate4 |awk '{print " <td> "$1 " </td>"}'`;
#new addition
export edate5=`date "+%Y%m%d" --date="5 day ago"`;
alrec5=`wc -l *ALERT*.dat|grep $edate5 |awk '{print " <td> "$1 " </td>"}'`;
aurec5=`wc -l *AUDIT*.dat|grep $edate5 |awk '{print " <td> "$1 " </td>"}'`;
carec5=`wc -l *CASE*.dat|grep $edate5 |awk '{print " <td> "$1 " </td>"}'`;
edrec5=`wc -l *EDLSTATUS*.dat|grep $edate5 |awk '{print " <td> "$1 " </td>"}'`;
curec5=`wc -l *CUST*.dat|grep $edate5 |awk '{print " <td> "$1 " </td>"}'`;
edlrec5=`wc -l *EDLRESP*.dat|grep $edate5 |awk '{print " <td> "$1 " </td>"}'`;
edlqrec5=`wc -l *EDLREQ*.dat|grep $edate5 |awk '{print " <td> "$1 " </td>"}'`;
pfrec5=`wc -l *PFR*.dat|grep $edate5 |awk '{print " <td> "$1 " </td>"}'`;
derec5=`wc -l *DEFAULTER*.dat|grep $edate5 |awk '{print " <td> "$1 " </td>"}'`;
orrec5=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate5 |awk '{print " <td> "$1 " </td>"}'`;
aprec5=`wc -l *APPLICATION_*.dat|grep $edate5 |awk '{print " <td> "$1 " </td>"}'`;
ordrec5=`wc -l *ORDERS_*.dat|grep $edate5 |awk '{print " <td> "$1 " </td>"}'`;
orderec5=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate5 |awk '{print " <td> "$1 " </td>"}'`;
carrec5=`wc -l *CAREQLOG_*.dat|grep $edate5 |awk '{print " <td> "$1 " </td>"}'`;
alirec5=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate5 |awk '{print " <td> "$1 " </td>"}'`;
frarec5=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate5 |awk '{print " <td> "$1 " </td>"}'`;




#NEW Addition
export edate6=`date "+%Y%m%d" --date="6 day ago"`;
alrec6=`wc -l *ALERT*.dat|grep $edate6 |awk '{print " <td> "$1 " </td>"}'`;
aurec6=`wc -l *AUDIT*.dat|grep $edate6|awk '{print " <td> "$1 " </td>"}'`;
carec6=`wc -l *CASE*.dat|grep $edate6|awk '{print " <td> "$1 " </td>"}'`;
edrec6=`wc -l *EDLSTATUS*.dat|grep $edate6|awk '{print " <td> "$1 " </td>"}'`;
curec6=`wc -l *CUST*.dat|grep $edate6|awk '{print " <td> "$1 " </td>"}'`;
edlrec6=`wc -l *EDLRESP*.dat|grep $edate6|awk '{print " <td> "$1 " </td>"}'`;
edlqrec6=`wc -l *EDLREQ*.dat|grep $edate6|awk '{print " <td> "$1 " </td>"}'`;
pfrec6=`wc -l *PFR*.dat|grep $edate6|awk '{print " <td> "$1 " </td>"}'`;
derec6=`wc -l *DEFAULTER*.dat|grep $edate6|awk '{print " <td> "$1 " </td>"}'`;
orrec6=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate6|awk '{print " <td> "$1 " </td>"}'`;
aprec6=`wc -l *APPLICATION_*.dat|grep $edate6|awk '{print " <td> "$1 " </td>"}'`;
ordrec6=`wc -l *ORDERS_*.dat|grep $edate6|awk '{print " <td> "$1 " </td>"}'`;
orderec6=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate6|awk '{print " <td> "$1 " </td>"}'`;
carrec6=`wc -l *CAREQLOG_*.dat|grep $edate6|awk '{print " <td> "$1 " </td>"}'`;
alirec6=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate6|awk '{print " <td> "$1 " </td>"}'`;
frarec6=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate6|awk '{print " <td> "$1 " </td>"}'`;

export edate7=`date "+%Y%m%d" --date="7 day ago"`;
alrec7=`wc -l *ALERT*.dat|grep $edate7 |awk '{print " <td> "$1 " </td>"}'`;
aurec7=`wc -l *AUDIT*.dat|grep $edate7|awk '{print " <td> "$1 " </td>"}'`;
carec7=`wc -l *CASE*.dat|grep $edate7|awk '{print " <td> "$1 " </td>"}'`;
edrec7=`wc -l *EDLSTATUS*.dat|grep $edate7|awk '{print " <td> "$1 " </td>"}'`;
curec7=`wc -l *CUST*.dat|grep $edate7|awk '{print " <td> "$1 " </td>"}'`;
edlrec7=`wc -l *EDLRESP*.dat|grep $edate7|awk '{print " <td> "$1 " </td>"}'`;
edlqrec7=`wc -l *EDLREQ*.dat|grep $edate7|awk '{print " <td> "$1 " </td>"}'`;
pfrec7=`wc -l *PFR*.dat|grep $edate7|awk '{print " <td> "$1 " </td>"}'`;
derec7=`wc -l *DEFAULTER*.dat|grep $edate7|awk '{print " <td> "$1 " </td>"}'`;
orrec7=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate7|awk '{print " <td> "$1 " </td>"}'`;
aprec7=`wc -l *APPLICATION_*.dat|grep $edate7|awk '{print " <td> "$1 " </td>"}'`;
ordrec7=`wc -l *ORDERS_*.dat|grep $edate7|awk '{print " <td> "$1 " </td>"}'`;
orderec7=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate7|awk '{print " <td> "$1 " </td>"}'`;
carrec7=`wc -l *CAREQLOG_*.dat|grep $edate7|awk '{print " <td> "$1 " </td>"}'`;
alirec7=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate7|awk '{print " <td> "$1 " </td>"}'`;
frarec7=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate7|awk '{print " <td> "$1 " </td>"}'`;


export edate8=`date "+%Y%m%d" --date="8 day ago"`;
alrec8=`wc -l *ALERT*.dat|grep $edate8 |awk '{print " <td> "$1 " </td>"}'`;
aurec8=`wc -l *AUDIT*.dat|grep $edate8|awk '{print " <td> "$1 " </td>"}'`;
carec8=`wc -l *CASE*.dat|grep $edate8|awk '{print " <td> "$1 " </td>"}'`;
edrec8=`wc -l *EDLSTATUS*.dat|grep $edate8|awk '{print " <td> "$1 " </td>"}'`;
curec8=`wc -l *CUST*.dat|grep $edate8|awk '{print " <td> "$1 " </td>"}'`;
edlrec8=`wc -l *EDLRESP*.dat|grep $edate8|awk '{print " <td> "$1 " </td>"}'`;
edlqrec8=`wc -l *EDLREQ*.dat|grep $edate8|awk '{print " <td> "$1 " </td>"}'`;
pfrec8=`wc -l *PFR*.dat|grep $edate8|awk '{print " <td> "$1 " </td>"}'`;
derec8=`wc -l *DEFAULTER*.dat|grep $edate8|awk '{print " <td> "$1 " </td>"}'`;
orrec8=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate8|awk '{print " <td> "$1 " </td>"}'`;
aprec8=`wc -l *APPLICATION_*.dat|grep $edate8|awk '{print " <td> "$1 " </td>"}'`;
ordrec8=`wc -l *ORDERS_*.dat|grep $edate8|awk '{print " <td> "$1 " </td>"}'`;
orderec8=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate8|awk '{print " <td> "$1 " </td>"}'`;
carrec8=`wc -l *CAREQLOG_*.dat|grep $edate8|awk '{print " <td> "$1 " </td>"}'`;
alirec8=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate8|awk '{print " <td> "$1 " </td>"}'`;
frarec8=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate8|awk '{print " <td> "$1 " </td>"}'`;

export edate9=`date "+%Y%m%d" --date="9 day ago"`;
alrec9=`wc -l *ALERT*.dat|grep $edate9|awk '{print " <td> "$1 " </td>"}'`;
aurec9=`wc -l *AUDIT*.dat|grep $edate9|awk '{print " <td> "$1 " </td>"}'`;
carec9=`wc -l *CASE*.dat|grep $edate9|awk '{print " <td> "$1 " </td>"}'`;
edrec9=`wc -l *EDLSTATUS*.dat|grep $edate9|awk '{print " <td> "$1 " </td>"}'`;
curec9=`wc -l *CUST*.dat|grep $edate9|awk '{print " <td> "$1 " </td>"}'`;
edlrec9=`wc -l *EDLRESP*.dat|grep $edate9|awk '{print " <td> "$1 " </td>"}'`;
edlqrec9=`wc -l *EDLREQ*.dat|grep $edate9|awk '{print " <td> "$1 " </td>"}'`;
pfrec9=`wc -l *PFR*.dat|grep $edate9|awk '{print " <td> "$1 " </td>"}'`;
derec9=`wc -l *DEFAULTER*.dat|grep $edate9|awk '{print " <td> "$1 " </td>"}'`;
orrec9=`wc -l *ORDERCREDITASSESSMENT_*.dat|grep $edate9|awk '{print " <td> "$1 " </td>"}'`;
aprec9=`wc -l *APPLICATION_*.dat|grep $edate9|awk '{print " <td> "$1 " </td>"}'`;
ordrec9=`wc -l *ORDERS_*.dat|grep $edate9|awk '{print " <td> "$1 " </td>"}'`;
orderec9=`wc -l *ORDERDELIVERYSTATUS_*.dat|grep $edate9|awk '{print " <td> "$1 " </td>"}'`;
carrec9=`wc -l *CAREQLOG_*.dat|grep $edate9|awk '{print " <td> "$1 " </td>"}'`;
alirec9=`wc -l *ALIASINGMATCHDETAIL_*.dat|grep $edate9|awk '{print " <td> "$1 " </td>"}'`;
frarec9=`wc -l *FRAUDASSESSMENTSTATUS_*.dat|grep $edate9|awk '{print " <td> "$1 " </td>"}'`;

echo "<table border = 1>"
echo "<tr><td colspan=3>Numbers of records generated</td></tr>"
echo "<tr><td>File Name </td><td>"$edate"</td><td>"$edate1"</td><td>"$edate2"</td><td>"$edate3"</td><td>"$edate4"</td><td>"$edate5"</td><td>"$edate6"</td><td>"$edate7"</td><td>"$edate8"</td><td>"$edate9"</td></tr>"
echo "<tr><td>ALERT </td>"$alrec"  "$alrec1"  "$alrec2"  "$alrec3"  "$alrec4"  "$alrec5"  "$alrec6"  "$alrec7"  "$alrec8"  "$alrec9"       </tr>"
echo "<tr><td>AUDIT </td>"$aurec"  "$aurec1"  "$aurec2"  "$aurec3"  "$aurec4"  "$aurec5"  "$aurec6"  "$aurec7"   "$aurec8"   "$aurec9"       </tr>"
echo "<tr><td>CASE  </td>"$carec"  "$carec1"  "$carec2"  "$carec3"  "$carec4"  "$carec5"  "$carec6"  "$carec7"   "$carec8"    "$carec9"       </tr>"
echo "<tr><td>EDLSTATUS  </td>"$edrec"    "$edrec1" "$edrec2" "$edrec3" "$edrec4" "$edrec5"  "$edrec6"  "$edrec7" "$edrec8" "$edrec9"          </tr>"
echo "<tr><td>CUST  </td>"$curec" "$curec1"   "$curec2"  "$curec3"   "$curec4"  "$curec5"  "$curec6"  "$curec7"   "$curec8"  "$curec9"        </tr>"
echo "<tr><td>EDLRESP  </td>"$edlrec" "$edlrec1" "$edlrec2"  "$edlrec3"   "$edlrec4"  "$edlrec5"  "$edlrec6"  "$edlrec7"   "$edlrec8"  "$edlrec9"         </tr>"
echo "<tr><td>EDLREQ  </td>"$edlqrec" "$edlqrec1"  "$edlqrec2"  "$edlqrec3"  "$edlqrec4"   "$edlqrec5"  "$edlqrec6" "$edlqrec7"  "$edlqrec8"   "$edlqrec9"    </tr>"
echo "<tr><td>PFR  </td>"$pfrec" "$pfrec1"  "$pfrec2" "$pfrec3" "$pfrec4"   "$pfrec5" "$pfrec6" "$pfrec7" "$pfrec8"   "$pfrec9"         </tr>"
echo "<tr><td>DEFAULTER  </td>"$derec" "$derec1"  "$derec2"   "$derec3"  "$derec4"  "$derec5"  "$derec6"   "$derec7"  "$derec8"  "$derec9"       </tr>"
echo "<tr><td>ORDERCREDITASSESSMENT</td>"$orrec"  "$orrec1"  "$orrec2"   "$orrec3"  "$orrec4" "$orrec5" "$orrec6"   "$orrec7"  "$orrec8" "$orrec9"   </tr>"
echo "<tr><td>APPLICATION  </td>"$aprec" "$aprec1" "$aprec2"  "$aprec3"   "$aprec4"  "$aprec5"   "$aprec6"  "$aprec7"   "$aprec8"  "$aprec9"     </tr>"
echo "<tr><td>ORDERS </td> "$ordrec" "$ordrec1"  "$ordrec2"   "$ordrec3"   "$ordrec4"  "$ordrec5" "$ordrec6"   "$ordrec7"   "$ordrec8"  "$ordrec9"    </tr>"
echo "<tr><td>ORDERDELIVERYSTATUS </td>"$orderec"  "$orderec1" "$orderec2"  "$orderec3"  "$orderec4"  "$orderec5" "$orderec6" "$orderec7" "$orderec8"  "$orderec9"  </tr>"
echo "<tr><td>CRARELOG  </td>"$carrec" "$carrec1" "$carrec2"  "$carrec3"   "$carrec4"  "$carrec5" "$carrec6"  "$carrec7"   "$carrec8"  "$carrec9"           </tr>"
echo "<tr><td>ALIASINGMATCHDETAIL</td>"$alirec" "$alirec1"   "$alirec2"   "$alirec3"   "$alirec4"   "$alirec5"   "$alirec6"   "$alirec7"  "$alirec8"  "$alirec9"             </tr>"
echo "<tr><td>FRAUDASSESSMENTSTATUS</td>"$frarec" "$frarec1"   "$frarec2"   "$frarec3"   "$frarec4"   "$frarec5"   "$frarec6"   "$frarec7"  "$frarec8"  "$frarec9"             </tr>"
echo "</table>"


echo "</body></html>"



>>>>>>> Stashed changes
