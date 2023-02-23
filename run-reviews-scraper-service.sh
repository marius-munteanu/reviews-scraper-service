#!/usr/bin/env bash

exec java \
-Djava.security.egd=file:/dev/./urandom \
-Dfile.encoding=UTF8 \
$JAVA_OPTIONS \
-jar \
/opt/abac/reviews-scraper-service.jar