
### ====================================================================== ###
##                                                                          ##
##  Bootstrap Script                                     ##
##                                                                          ##
### ====================================================================== ###

DIRNAME=`dirname $0`

# Setup @PROJECT_NAME@
if [ "x$@PROJECT_NAME@" = "x" ]; then
    # get the full path (without any relative bits)
    @PROJECT_NAME@=`cd $DIRNAME; pwd`
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
runjar="$@PROJECT_NAME@/@PROJECT_NAME@.jar"
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
         @MAIN_CLASS@ "$@" &
