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
