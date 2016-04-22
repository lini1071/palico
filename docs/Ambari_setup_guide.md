## Apache Ambari 설치 가이드

### 순서

 1. 개요
 2. 설치 전 준비
 3. 설치
 4. 클러스터 설정


#### 1. 개요

 Apache Ambari는 Hortonworks에서 개발한 Apache Hadoop 및 연계 프로그램의 통합 관리 솔루션이다. 설치 측면에서는 메인 서버에 Ambari Server를 설치하고 메인 서버와 에이전트가 될 나머지 호스트들 간에 SSH 연결을 수립해주기만 하면, 클러스터 내의 모든 호스트에 관련 소프트웨어 패키지를 웹 서버를 이용해 구현된 GUI로 편하게 설치할 수 있다. 관리 측면에서는 서버에서 출력하여 주는 웹페이지에서 HDFS의 점유 및 가용 용량, YARN을 통한 클러스터 내 CPU 및 메모리 리소스 사용량 등을 시각적으로 확인할 수 있게 해준다.  
 본 문서는 Apache Ambari 2.2.1을 설치하고 해당 프로그램을 이용해 하나의 Cluster를 구축하는 방법을 기술한다. 택한 설치 방식은 이미 빌드된 패키지를 다운로드 받는 방식을 취했는데, 이는 현재 배포되는 2.2.1 버전 소스가 제대로 빌드되지 않기 때문이다.  
 본 문서에서는 Server와 Agent 모두 Ubuntu 14.04.4를 사용한다고 가정한다. 또한 본 문서에서 Ambari Server가 기본적으로 사용하는 PostgreSQL 대신 MySQL을 사용한다고 가정한다. 본 문서에서 설명하지 않은 부분에 대한 내용이 필요할 경우 다음 웹페이지를 참조할 수 있다.

<a href="http://docs.hortonworks.com/HDPDocuments/Ambari-2.2.1.1/bk_Installing_HDP_AMB/content/index.html">Automated Install with Ambari</a>

#### 2. 설치 전 준비

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


#### 3. 설치

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

#### 4. 클러스터 설정

![ambari-server web login](/docs/images/screenshot_ambari-server_web-login.png)
클러스터를 구축하기 위해 8080 포트의 web server로 접속을 시도하면 다음과 같은 화면을 볼 수 있다. admin 계정의 기본 설정값은 ID/PW admin/admin으로 되어 있다.

