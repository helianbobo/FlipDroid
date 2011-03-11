
### ====================================================================== ###
##                                                                          ##
##  tika.it API Server Bootstrap Script                                     ##
##                                                                          ##
### ====================================================================== ###

DIRNAME=`dirname $0`

# Setup TIKA_API_HOME
if [ "x$TIKA_API_HOME" = "x" ]; then
    # get the full path (without any relative bits)
    TIKA_API_HOME=`cd $DIRNAME/..; pwd`
fi

# Setup the JVM
if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
	JAVA="$JAVA_HOME/bin/java"
    else
	JAVA="java"
    fi
fi

# Setup the classpath
runjar="$TIKA_API_HOME/bin/tika-api.jar"
if [ ! -f "$runjar" ]; then
    die "Missing required file: $runjar"
fi
BOOT_CLASSPATH="$runjar"


for i in `ls ./lib/*.jar`
do
  BOOT_CLASSPATH=${BOOT_CLASSPATH}:${i}
done


JAVA_OPTS="-Xmx256M -XX:PermSize=32M"

"$JAVA" $JAVA_OPTS \
         -classpath "$BOOT_CLASSPATH" \
         it.tika.APIServer "$@" &