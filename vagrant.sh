#!/bin/sh

# Sette opp difi-bruker
if [ ! -d /home/difi ]; then
	useradd -U -m -G admin -s /bin/bash -p FX317d7avEp4s difi
fi

# Installere avhengigheter
aptitude update
aptitude install -y git curl openjdk-7-jdk tomcat7 maven apache2

# Tomcat 7
echo '
export JAVA_OPTS="-Dcom.sun.management.jmxremote=true \
                  -Dcom.sun.management.jmxremote.port=9090 \
                  -Dcom.sun.management.jmxremote.ssl=false \
                  -Dcom.sun.management.jmxremote.authenticate=false \
                  -Djava.rmi.server.hostname=192.168.50.60"

' > /usr/share/tomcat7/bin/setenv.sh
chmod a+x /usr/share/tomcat7/bin/setenv.sh

# Apache
ln -s /home/difi/site /var/www/site

# Kommandoer
echo '#!/bin/sh

cd /vagrant
mvn clean package $*

if [ -d /vagrant/target/site ]; then
	if [ -d /home/difi/site ]; then
		rm -r /home/difi/site
	fi
	cp -r /vagrant/target/site /home/difi/site
fi

' > /usr/bin/dh-build

echo '#!/bin/sh

cd /vagrant
mvn jetty:run $*
' > /usr/bin/dh-jetty

# TODO
echo '#!/bin/sh

' > /usr/bin/dh-deploy

echo '#!/bin/sh

/usr/bin/dh-build $*
/usr/bin/dh-deploy
' > /usr/bin/dh-full

chmod a+x /usr/bin/dh-build /usr/bin/dh-deploy /usr/bin/dh-full /usr/bin/dh-jetty
