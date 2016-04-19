## Apache Ambari 설치 가이드 ##

### 순서 ###

1. 개요
2. 설치 전 준비
3. 설치
4. 환경 설정


#### 1. 개요 ####

Apache Ambari는 Hortonworks에서 개발한 Apache Hadoop 및 연계 프로그램의 통합 관리 솔루션이다. 설치 측면에서는 메인 서버에 Ambari Server를 설치하고 메인 서버와 에이전트가 될 나머지 호스트들 간에 SSH 연결을 수립해주기만 하면, 클러스터 내의 모든 호스트에 관련 소프트웨어 패키지를 웹 서버를 이용해 구현된 GUI로 편하게 설치할 수 있다. 관리 측면에서는 서버에서 출력하여 주는 웹페이지에서 HDFS의 점유 및 가용 용량, YARN을 통한 클러스터 내 CPU 및 메모리 리소스 사용량 등을 시각적으로 확인할 수 있게 해준다. 본 문서는 Apache Ambari 2.2.1을 설치하고 해당 프로그램을 이용해 하나의 Cluster를 구축하는 방법을 기술한다.

본 문서에서는 Server와 Agent 모두 Ubuntu 14.04.4를 사용한다고 가정한다. 또한 본 문서에서 Ambari Server가 기본적으로 사용하는 PostgreSQL 대신 MySQL을 사용한다고 가정한다. 본 문서에서 설명하지 않은 부분에 대한 내용이 필요할 경우 다음 웹페이지를 참조할 수 있다.

<a href="http://docs.hortonworks.com/HDPDocuments/Ambari-2.2.1.1/bk_Installing_HDP_AMB/content/index.html">Automated Install with Ambari</a>

#### 2. 설치 전 준비 ####

모든 호스트는 /etc/hosts 파일에 클러스터를 구성할 모든 노드들의 IP와 호스트명을 기입해주어야 한다. 다음 ambari-server에서 각 agent들에 대해 자동으로 관리를 수행할 수 있도록 root 계정 접근을 허가한다. ambari-server를 실행할 호스트는 ssh-keygen으로 rsa 공개-비공개 키 pair를 생성한 뒤, 여기서 나오는 id_rsa.pub 공개 키를 agent가 될 호스트 모두에게 전송한다.
agent가 될 호스트는 openssh-server를 설치한 뒤 server에서 전송한 id_rsa.pub를 받아 /root 폴더의 .ssh/authorized_keys 파일에 그 내용을 append하여 저장하면 된다. server 호스트에서도 agent를 실행하게끔 할 경우도 이와 같게 openssh-server 설치 후 같은 설정 절차를 거쳐야 한다.

```shell
sudo mkdir /root/.ssh
sudo cat id_rsa.pub >> /root/.ssh/authorized_keys
```

필요할 경우 ufw에 수동 설정을 해주거나, 아니면 이를 비활성화시켜 ambari가 설치하는 서비스들이 사용할 port들의 blocking을 방지하도록 한다.



#### 3. 설치 ####
