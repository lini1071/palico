# Apache Ambari 설치 및 사용 가이드

## 순서

1. 개요
2. 설치 전 준비
3. 설치
4. 클러스터 설정
  1. Logging in & preparing
  2. Step 0 - Naming the cluster
  3. Step 1 - Selecting HDP stack version
  4. Step 2 - Targeting host(agent)s
  5. Step 3 - Confirming agent hosts
  6. Step 4 - Selecting services to use
  7. Step 5 - Assigning master agent
  8. Step 6 - Assigning slave agents
  9. Step 7 - Customizing each service
  10. Step 8 - Review selected services
5. 작업 수행 및 UI를 통한 내역 확인
  1. Submitting Application (jar)
  2. Monitoring services
  3. More information

### 1. 개요

 Apache Ambari는 Hortonworks에서 개발한 Apache Hadoop 및 연계 프로그램의 통합 관리 솔루션이다. 설치 측면에서는 메인 서버에 Ambari Server를 설치하고 메인 서버와 에이전트가 될 나머지 호스트들 간에 SSH 연결을 수립해주기만 하면, 클러스터 내의 모든 호스트에 관련 소프트웨어 패키지를 웹 서버를 이용해 구현된 GUI로 편하게 설치할 수 있다. 관리 측면에서는 서버에서 출력하여 주는 웹페이지에서 HDFS의 점유 및 가용 용량, YARN을 통한 클러스터 내 CPU 및 메모리 리소스 사용량 등을 시각적으로 확인할 수 있게 해준다.  
 본 문서는 Apache Ambari 2.2.1을 설치하고 해당 프로그램을 이용해 하나의 Cluster를 구축하는 방법을 기술한다. 택한 설치 방식은 이미 빌드된 패키지를 다운로드 받는 방식을 취했는데, 이는 현재 배포되는 2.2.1 버전 소스가 제대로 빌드되지 않기 때문이다.  
 본 문서에서는 Server와 Agent 모두 Ubuntu 14.04.4를 사용한다고 가정한다. 또한 본 문서에서 Ambari Server가 기본적으로 사용하는 PostgreSQL 대신 MySQL을 사용한다고 가정한다. 본 문서에서 설명하지 않은 부분에 대한 내용이 필요할 경우 다음 웹페이지를 참조할 수 있다.

<a href="http://docs.hortonworks.com/HDPDocuments/Ambari-2.2.1.1/bk_Installing_HDP_AMB/content/index.html">Automated Install with Ambari</a>

### 2. 설치 전 준비

 모든 호스트는 /etc/hosts 파일에 클러스터를 구성할 모든 노드들의 IP와 호스트명을 기입해주어야 한다. 다음 ambari-server에서 각 agent들에 대해 자동으로 관리를 수행할 수 있도록 root 계정 접근을 허가한다. ambari-server를 실행할 호스트는 ssh-keygen으로 rsa 공개-비공개 키 pair를 생성한 뒤, 여기서 나오는 id_rsa.pub 공개 키를 agent가 될 호스트 모두에게 전송한다.  
 agent가 될 호스트는 openssh-server를 설치한 뒤 server에서 전송한 id_rsa.pub를 받아 /root 폴더의 .ssh/authorized_keys 파일에 그 내용을 append하여 저장하면 된다. server 호스트에서도 agent를 실행하게끔 할 경우도 이와 같게 openssh-server 설치 후 같은 설정 절차를 거쳐야 한다.

```shell
sudo mkdir /root/.ssh
sudo cat id_rsa.pub >> /root/.ssh/authorized_keys
```

 이 절차를 수행 후 server 호스트에서 해당 agent 호스트의 root 계정으로 ssh 접속이 가능한지 확인해주면 된다. 이 부분에 대한 내용은 다음 두 링크에서 자세히 설명하고 있다.

<a href="http://askubuntu.com/questions/46930/how-can-i-set-up-password-less-ssh-login">How can I set up password-less SSH login?</a>  
<a href="https://help.ubuntu.com/community/SSH/OpenSSH/Keys">SSH/OpenSSH/Keys</a>

 ufw의 경우는 필요할 경우 수동 설정을 해주거나, 아니면 이를 비활성화시켜 ambari가 설치하는 서비스들이 사용할 port들의 blocking을 방지하도록 한다. ambari는 시간 동기화를 위해 ntp의 사용을 권하는데 Ubuntu에서는 sudo apt-get install ntp로 설치할 수 있다.


### 3. 설치

 server 호스트의 터미널에서 다음과 같이 입력하는 절차를 거쳐 ambari-server를 설치한다.

```shell
cd /etc/apt/sources.list.d
sudo wget http://public-repo-1.hortonworks.com/ambari/ubuntu14/2.x/updates/2.2.1/ambari.list
sudo apt-key adv --recv-keys --keyserver keyserver.ubuntu.com B9733A7A07513CAD
sudo apt-get update
sudo apt-get install ambari-server
```

wget의 repository 주소 목록은 다음에 등재되어 있다.  
<a href="http://docs.hortonworks.com/HDPDocuments/Ambari-2.2.1.1/bk_Installing_HDP_AMB/content/_ambari_repositories.html">
Ambari repositories
</a>

ambari-server를 실행하기 위해서는 root 권한이 있어야 한다. ambari-server를 실행하기 전 사용자 정의 환경설정을 해야 할 경우 ambari-server setup을 입력한다. server 호스트가 JDK를 기본 경로가 아닌 별도의 경로에 설치했을 경우, PostgreSQL이 아닌 다른 SQL을 사용할 경우는 setup을 통해 설정해주어야 한다.

![Running ambari-server setup](/docs/images/screenshot_ambari-server_setup.png)

ambari-server setup을 실행하면 스크린샷과 같이 ambari-server daemon이 사용할 JDK와 Database에 관한 사항을 설정하게 된다.

MySQL을 새로 설치한 뒤 Ambari에서 이와 연동 가능하도록 설정하고자 할 경우 아래의 절차를 따른다.

```shell
sudo apt-get install mysql-server
sudo apt-get install mysql-connector-java
mysql -u root -p<root_password>
> create user 'ambari'@'%' identified by '<ambari_password>';
> grant all privileges on *.* 'ambari'@'%' identified by '<ambari_password>';
> flush privileges;
> exit
mysql -u ambari -p<ambari_password>
> create database ambari;
> use ambari;
> source /var/lib/ambari-server/resources/Ambari-DDL-MySQL-CREATE.sql;
> exit
```
이전 setup에서 ambari-server가 사용할 database user명은 ambari로, database의 이름 역시 ambari로 설정했다고 가정한다. setup에서 기본으로 사용하려는 ambari_password 값은 bigdata이다. MySQL이 아닌 다른 SQL에 관한 내용, 또는 조금 더 자세한 내용이 필요할 경우 아래의 문서를 참고한다.  
<a href="http://docs.hortonworks.com/HDPDocuments/Ambari-2.2.1.1/bk_ambari_reference_guide/content/_using_non-default_databases_-_ambari.html">Using non-default databases</a>

설정이 완료되었으면 ambari-server start를 통해 서버 프로세스가 시작하는지 확인한다.
![Running ambari-server start](/docs/images/screenshot_ambari-server_start.png)

### 4. 클러스터 설정

#### 4.1 Logging in & preparing

![ambari-server web login](/docs/images/screenshot_ambari-server_web-login.png)
클러스터를 구축하기 위해 8080 포트의 web server로 접속을 시도하면 다음과 같은 화면을 볼 수 있다. admin 계정의 기본 설정값은 ID/PW admin/admin으로 되어 있다.


![ambari-server manage Ambari](/docs/images/screenshot_ambari-server_manage.png)
정상적으로 로그인이 수행되면 다음 화면이 나온다. Launch Install Wizard를 눌러 설치 마법사를 실행한다.

#### 4.2 Step 0 - Naming the cluster

![ambari-server cluster install step 0](/docs/images/screenshot_ambari-server_cluster-install_step0.png)
클러스터의 이름을 정해주는 단계이다.


#### 4.3 Step 1 - Selecting HDP stack version

![ambari-server cluster install step 1](/docs/images/screenshot_ambari-server_cluster-install_step1.png)
HDP(Hortonworks Data Platform) 서비스 스택 버전을 선택하는 단계이다. 작성일(Apr/22/2016) 기준으로 2.1부터 2.4까지의 네 가지 버전을 선택할 수 있다. HDP 2.4의 경우는 debian7, redhat6&7, suse11, ubuntu12&14의 6가지 ost에 대한 Ambari agent 자동 설치를 지원한다. Ubuntu 14의 경우 14.04.4는 ubuntu14로 인식하나 그 이전의 버전으로 설치가 되어 있을 경우 ubuntu14가 아닌 다른 os로 인식하여 자동 설치가 지원되지 않는 경우가 발생할 수 있으므로 유의해야 한다.


#### 4.4 Step 2 - Targeting host(agent)s

![ambari-server cluster install step 2](/docs/images/screenshot_ambari-server_cluster-install_step2.png)
이 단계부터 실질적인 클러스트 구성을 계획하는 단계라고 할 수 있다. /etc/hosts에 등록해둔 agent가 될 호스트들의 hostname을 한줄씩 기입한다. 또는 기재되어 있듯이 명칭에서 일련의 규칙성을 가지며 숫자로 구별되는 여러 호스트들을 Pattern Expressions를 통해 등록할 수도 있다. Host registration information에서 이 문서에서처럼 SSH 연결을 준비했고 이를 사용하는 경우 ssh-keygen을 실행하여 생성된 id_rsa (priavte key, not .pub) 파일을 선택한다. 이후 Register and Confirm 버튼을 선택하면 agent가 될 각 호스트에 ambari-agent의 설치를 시도한다.


#### 4.5 Step 3 - Confirming agent hosts

![ambari-server cluster install step 3](/docs/images/screenshot_ambari-server_cluster-install_step3.png)
Host의 Status는 처음에 Installing에서 시작해, 정상적으로는 Registering을 거쳐 Success로 작업이 완료된다. Success가 되는 경우 명단에 오른 host들에 ambari-agent 설치가 완료되었다는 뜻이며 apt-get remove ambari-agent로 이를 삭제한 뒤 본 단계를 다시 반복할 수 있다. 아래에 안내문 형식으로 경고가 발생할 수 있는데 Transparent Huge Page Issues의 경우는 hugepages 패키지를 설치한 뒤 hugeadm --thp-never를 실행했음에도 경고가 사라지지 않음을 확인했다.


#### 4.6 Step 4 - Selecting services to use

![ambari-server cluster install step 4](/docs/images/screenshot_ambari-server_cluster-install_step4.png)
클러스터에 설치하기를 원하는 서비스를 선택하는 단계이다. 여기에서 자신이 원하는 것만을 고른다 하더라도 ambari web interface에서 추가적으로 필요한 기능을 반드시 설치하게끔 한다(OK를 누르지 않으면 설치가 더 진행되지 않는다).


#### 4.7 Step 5 - Assigning master agent

![ambari-server cluster install step 5](/docs/images/screenshot_ambari-server_cluster-install_step5.png)
각 서비스들의 daemon을 실행할 master host를 설정하는 단계이다. 이 단계에서는 HDFS의 NameNode, YARN의 ResourceManager와 같은 필수(master) daemon의 실행 위치를 결정한다.


#### 4.8 Step 6 - Assigning slave agents

![ambari-server cluster install step 6](/docs/images/screenshot_ambari-server_cluster-install_step6.png)
이 단계에서는 각 host마다 부수적(slave)인 daemon들의 실행 여부를 결정하는 단계이다. HDFS의 DataNode, YARN의 NodeManager 등이 이에 해당한다.


#### 4.9 Step 7 - Customizing each service

![ambari-server cluster install step 7](/docs/images/screenshot_ambari-server_cluster-install_step7.png)
이 단계에서는 각 서비스의 세부 config을 설정하는 단계이다. 기본 세팅 탭에서는 MapReduce의 memory 할당량과 같은 값을 설정할 수 있다. HDFS의 경우 Advanced 탭에서는 General에서 block replication 정도를 결정하는 dfs.replication을, Advanced hdfs-site에서 dfs의 한 block 당 size 값이 될 dfs.blocksize, dfs의 각 파일에 접근 권한을 설정할 지의 여부인 dfs.permissions.enabled 등의 값을 설정할 수 있다.

![ambari-server cluster install step 7 - setting hive password](/docs/images/screenshot_ambari-server_cluster-install_step7-hive.png)
이 단계에서는 Hive Metastore의 database password를 설정해주어야 다음 단계로 넘어갈 수 있다.


#### 4.10 Step 8 - Review selected services

![ambari-server cluster install step 8](/docs/images/screenshot_ambari-server_cluster-install_step8.png)
선택한 내역들을 확인시켜주는 단계이다. Deploy를 누르면 ambari-agent를 통해 선택한 내용들을 설치하는 준비를 시작한다.

![ambari-server cluster install step 9](/docs/images/screenshot_ambari-server_cluster-install_step9.png)
설치 진행 내역이 표시되며, 해당 작업에 실패한 경우 그 오류를 확인할 수 있어 이를 수정한 뒤 재시도할 수 있다.


### 5. 작업 수행 및 UI를 통한 내역 확인

#### 1. Submitting Application (jar)
![client command in bash shell](/docs/images/screenshot_client_comm_shell.png)  
agent host에 YARN client가 설치되어 있는 경우 터미널에서 yarn jar 명령을 통해 jar application을 submit할 수 있다. 마찬가지로 hadoop jar, spark-submit 등도 client가 설치되어 있을 시 가능하다. 다만 hadoop jar는 YARN이 설치되어 있는 환경에서는 권장되지 않는다.

![executing application using yarn jar](/docs/images/screenshot_executing_application_using_YARN.png)  
다음은 yarn jar로 application을 submit하는 예제를 갈무리한 것이다. 빨간 줄 부분은 전달할 jar 파일의 이름을 기입하는 필수 구문이고, 뒤에 붙는 파란 줄 부분은 application에서 요구하는 부수 arguments이다.

#### 2. Monitoring services
![Ambari server web UI dashboard](/docs/images/screenshot_ambari_dashboard_all.png)  
Ambari Server Web UI는 각 구성요소마다 나뉘어진 모니터링 기능을 한 관리 페이지에서 할 수 있게끔 해준다. Dashboard에서는 현재 클러스터의 전체적인 가용 수치 및 자원 사용량 등을 모니터링할 수 있다. 기본적으로는 스크린샷에서 나온 것과 같이 HDFS와 YARN 환경에 관한 내역을 표시해준다. 

![Ambari server web UI service monitor HDFS](/docs/images/screenshot_ambari_service-monitor_HDFS.png)  
![Ambari server web UI service monitor YARN](/docs/images/screenshot_ambari_service-monitor_YARN.png)  
각 구성요소마다에 대한 모니터링도 가능하다. 주로 NameNode의 데이터들을 나타내는 HDFS 환경 상황 모니터링은 HDFS 탭에서, MapReduce를 수행할 때의 CPU 점유율과 메모리 사용량 등은 YARN 탭에서 모니터링이 가능하다. 각 서비스 모니터는 Web UI 우측 상단의 Services에서 해당하는 서비스 이름을 선택해서 확인할 수 있다.

![Ambari server web UI Quick Links HDFS](/docs/images/screenshot_services_quick-links_HDFS.png)  
![Ambari server web UI Quick Links MapReduce](/docs/images/screenshot_services_quick-links_MapReduce.png)  
![Ambari server web UI Quick Links YARN](/docs/images/screenshot_services_quick-links_YARN.png)  
HDFS, MapReduce, YARN, 그리고 Spark의 경우는 위와 같이 Quick Links라는 메뉴가 추가되어 이를 통해 각 서비스를 관리하는 node의 Web Application 서버로 접속할 수 있게 해준다. HDFS의 Quick Links는 DFS의 관리를 담당하는 NameNode UI로 연결되어 이 부분에 대한 중요도는 다소 낮다. Application의 시작 및 종료 그리고 작업 내역은 MapReduce와 YARN의 Quick Links를 통해 각각 JobHistory, ResourceManager 서버로 접속하여 확인할 수 있다.

#### 3. More informations
본 단락에서는 5.2에서 Quick Links를 통하여 HDFS, MapReduce, YARN의 UI에 접속하였을 때의 내용을 기술한다. 단, Quick Links를 통하여 확인할 수 있는 각 서비스의 logs, JMX, Thread stacks에 대한 내용은 기술하지 않는다. 또한 본 문서의 예제로서 제시되는 스크린샷의 MapReduce의 JobHistory와 YARN ResourceManager 작업 내역은 모두 yarn jar를 통해 전달된 MapReduce 어플리케이션으로서 수행되었음을 미리 표기한다.

##### HDFS NameNode

![Hadoop NameNode WebApp UI Overview](/docs/images/screenshot_HDFS_namenode_webapp_overview.png)  
Services의 HDFS 탭에서 NameNode UI를 선택했을 때는 다음과 같은 WebAPP UI를 확인할 수 있다. Summary에 DFS의 상황이 요약되어 있다.

![Hadoop NameNode WebApp UI Datanodes Monitor](/docs/images/screenshot_HDFS_namenode_webapp_datanodes-monitor.png)  
상단의 Datanodes 탭에서는 현재 DFS 시스템에서 연결된 모든 DataNode들과 각각의 상태를 간략히 볼 수 있다.

![Hadoop NameNode WebApp UI FileSystem Explorer](/docs/images/screenshot_HDFS_namenode_webapp_fs-explorer.png)  
Utilities 탭의 Browse the file system을 선택하면 다음과 같이 Web UI에서 DFS 안의 파일들을 탐색할 수 있다. 파일 내용들을 바로 확인할 수는 없지만 각 파일을 다운로드할 수 있어서 이후 확인이 가능하다.

##### MapReduce2 JobHistory

![MapReduce2 JobHistory WebApp UI Main](/docs/images/screenshot_MapReduce_jobhistory_main.png)  
MapReduce2의 Quick Links에서 JobHistory UI를 선택했을 때의 Web UI 화면이다. 이 기본 화면의 출력은 좌측 Application 탭의 Jobs를 선택한 경우와 동일하다. About은 build version과 JobHistory server daemon 시작 시간만을 출력해주기 때문에 스크린샷을 통한 추가 설명을 생략한다.

![MapReduce2 JobHistory WebApp UI Job overview](/docs/images/screenshot_MapReduce_jobhistory_job.png)  
수행된 job 목록 중 하나를 택했을 경우 다음과 같은 Job overview 화면을 볼 수 있다. 여기서 Node의 링크를 클릭했을 때는 DataNode WebApp UI로 이동하게 되며 node에 관한 간략한 내용을 보여준다. Task type과 Attempt Type을 Map과 Reduce의 둘로 나누어 두었으며, attempt의 경우는 failed, killed, successful의 세 가지 경우로 확인할 수 있다. Map과 Reduce의 Task type으로 나눈 작업 내역은 좌측에 새로 생긴 Job 탭을 통해서도 확인할 수 있다(같은 페이지로 링크되어 있음).

![MapReduce2 JobHistory WebApp UI Job Counters](/docs/images/screenshot_MapReduce_jobhistory_job_counters.png)  
좌측 Job 탭의 Counters를 선택하면 다음과 같이 해당 job의 수행 결과 저장된 Counter 값들을 표시하여 준다. Name의 각 항목들을 클릭하면 (m|r)_(4d)의 형태로 작업 유형과 task 번호마다에 대한 값을 추가적으로 확인할 수 있다.  
MapReduce에서 정의하는 기본 Counter들의 목록은 스크린샷의 내용과 같으며, JobHistory server에서 확인할 수 있는 Counter들의 값은 스크린샷과 같이 5가지로 나눌 수 있다. File System Counters 항목은 주로 HDFS에서 요청된 I/O Operation 수와 그 데이터 크기를 저장한다. Job Counters 항목은 map/reduce task 수와 task에 걸린 시간을 저장하며, Map-Reduce Framework 항목에는 CPU 사용시간, GC 호출 시간, I/O 레코드 수 및 힙·스냅샷 크기 등의 Counter들이 저장된다. 나머지 두 개 중 하나는 File Input Format Counter ; 읽은(read) 바이트 크기, 또 하나는 File Output Format Counter ; 쓴(write) 바이트 크기를 저장한다. 

![MapReduce2 JobHistory WebApp UI Job Configs](/docs/images/screenshot_MapReduce_jobhistory_job_conf.png)  
Job의 Configuration을 선택하면 해당 job을 수행했을 당시의 설정(config) 값들을 확인할 수 있다. 

![MapReduce2 JobHistory WebApp UI Job Map Tasks](/docs/images/screenshot_MapReduce_jobhistory_job_map-tasks.png)  
다음은 클러스터에서 실행한 job들 중 하나를 골라 그 map task들을 확인한 내역이다. 작업의 수행 여부와 시작 및 종료 시각, 수행 시간을 확인할 수 있으며 각 map task 항목을 클릭하면 해당 task가 수행된 node와 저장된 log 내역을 추가적으로 확인할 수 있다.

![MapReduce2 JobHistory WebApp UI Config](/docs/images/screenshot_MapReduce_jobhistory_conf.png)  
Tools 탭의 Configuration을 선택했을 때는 다음과 같이 mapreduce, mapred에 관련된 현재 설정 값을 xml 형태로 표시해준다.


##### YARN ResourceManager

![YARN ResourceManager WebApp UI Screenshot](/docs/images/screenshot_YARN_resource-manager.png)  
다음은 Services의 YARN 탭에서 ResourceManager UI를 선택했을 때의 WebAPP UI 메인 페이지 화면을 스크린샷으로 저장한 것이다.

![YARN ResourceManager WebApp UI Cluster Node list](/docs/images/screenshot_YARN_resource-manager_cluster_nodes.png)  
좌측 Cluster 탭의 Nodes를 통해 현재 클러스트의 노드 리스트를 확인할 수 있다.

![YARN ResourceManager WebApp UI Cluster Applications](/docs/images/screenshot_YARN_resource-manager_applications.png)  
Applications 메뉴에서는 수행 대기 중이거나 수행 중인, 또는 수행 종료된 어플리케이션 목록을 확인할 수 있다. 어플리케이션 유형이 MAPREDUCE(MapReduce)인 경우 각 항목에서 Tracking UI 열의 History를 클릭하면 위의 JobHistory server UI 페이지로 이동한다. 이미 완료된 작업의 Counters 값을 확인해야 하는 경우 등은 이 순서를 따른다.

![YARN ResourceManager WebApp UI Cluster Applications Running](/docs/images/screenshot_YARN_resource-manager_applications_running.png)  
![YARN ResourceManager WebApp UI Cluster Applications Running Attempt](/docs/images/screenshot_YARN_resource-manager_applications_running_attempt.png)  
![YARN ResourceManager WebApp UI Cluster Applications Tracking Running](/docs/images/screenshot_YARN_resource-manager_applications_running_tracking.png)  
현재 실행 중(RUNNING)인 어플리케이션이 있다면 RUNNING 페이지에 그 내용이 등재되며 리소스 요청 내역과 각 호스트의 로그들을 열람할 수 있고, 또한 Tracking UI가 ApplicationMaster로 표기되어 작업 진행 상황을 확인할 수 있다.

![YARN ResourceManager WebApp UI Cluster Scheduler](/docs/images/screenshot_YARN_resource-manager_scheduler.png)  
Scheduler에서는 스케쥴러 내부 상태와 그에 할당된 자원 등을 표시하여 준다.

![YARN ResourceManager WebApp UI Config](/docs/images/screenshot_YARN_resource-manager_conf.png)  
Tools 탭에서 Configuration을 선택했을 때는 다음과 같이 ResourceManager에 관련된 현재 설정 값을 xml 형태로 표시해준다. JobHistory의 것과 비슷하나 diff의 적용 결과를 보았을 때 내용이 완전히 동일하지는 않고 미세한 차이가 있다. 주된 차이가 발생한 항목들은 YARN과 밀접한 설정은 ResourceManager에서, YARN보다 Hadoop과 밀접한 설정에서는 JobHistory에서 각 property의 source가 programatically로 되어있다는 점이다. 또한 같은 property value를 갖게 되나 두 daemon 사이에 property name을 달리 사용하는 경우에 대해서도 차이가 발생한다.
