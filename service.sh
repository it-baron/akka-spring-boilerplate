#!/bin/sh
NAME="service"
DESC="service"
PROFILE=prod

RUNDIR=/opt/${NAME}
PIDFILE=/var/run/${NAME}.pid

PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin

JAR_NAME=${NAME}.jar
JAVA_HOME=/usr/lib/jvm/java-8-oracle
DAEMON=${JAVA_HOME}/bin/java
DAEMON_ARGS="-Dspring.profiles.active=${PROFILE} -Djava.security.egd=file:/dev/./urandom -jar $JAR_NAME $RUNDIR"
JAR_PATH=${RUNDIR}/${JAR_NAME}

# Exit if the package is not installed
if [ ! -x "$DAEMON" ]; then
{
  echo "Couldn't find executable $DAEMON"
  exit 99
}
fi

# Exit if the package is not installed
if [ ! -f "$JAR_PATH" ]; then
{
  echo "Couldn't find jar $JAR_PATH"
  exit 99
}
fi

set -e

export JAVA_HOME

set_pidfile()
{
  pgrep -f "$DAEMON[[:space:]]*$DAEMON_ARGS" > ${PIDFILE}
}

case "$1" in
    start)
        if [ -f "$PIDFILE" ]; then {
            echo "Already started"
            exit 98
        }
        fi

        echo -n "Starting $DESC: "
        mkdir -p ${RUNDIR}

        if [ -n "$ULIMIT" ]
        then
            ulimit -n ${ULIMIT}
        fi

        if start-stop-daemon --start --quiet --umask 007 --chdir ${RUNDIR} -b --name ${NAME} --pidfile ${PIDFILE} --exec ${DAEMON} -- ${DAEMON_ARGS}
        then
            touch ${PIDFILE}
            set_pidfile
            echo "$NAME."
        else
            echo "failed."
        fi
        ;;

    stop)
        echo -n "Stopping $DESC: "
        if start-stop-daemon --stop --retry forever/TERM/1 --quiet --oknodo --pidfile ${PIDFILE} --exec ${DAEMON}
        then
            echo "$NAME."
        else
            echo "failed."
        fi
        rm -f ${PIDFILE}
        sleep 1
        ;;

    restart|force-reload)
        ${0} stop
        ${0} start
        ;;

    status)
        echo -n "$DESC is "
        if start-stop-daemon --stop --quiet --signal 0 --name ${NAME} --pidfile ${PIDFILE}
        then
            echo "running"
        else
            echo "not running"
            exit 1
        fi
        ;;

    *)
        echo "Usage: $NAME {start|stop|restart|force-reload}" >&2
        exit 1
        ;;

esac

exit 0