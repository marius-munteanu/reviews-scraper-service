FROM openjdk:17-alpine
RUN apk update && apk add bash

MAINTAINER Marius Catalin Munteanu

RUN mkdir -p /etc/abac/reviews-scraper-service  \
    /var/log/abac/reviews-scraper-service

#RUN chown -R abac /var/log/abac/reviews-scraper-service

VOLUME [ \
"/etc/abac/reviews-scraper-service", \
"/var/log/abac/reviews-scraper-service" \
]

WORKDIR /opt/abac

RUN pwd && ls -l

ADD run-reviews-scraper-service.sh /opt/abac/run-reviews-scraper-service.sh
ADD review-scraper-service-application/target/reviews-scraper-service-1.0.0-SNAPSHOT.jar /opt/abac/reviews-scraper-service.jar

RUN sh -c 'touch /opt/abac/reviews-scraper-service.jar'

# Use the unprivileged user
#USER abac

ENTRYPOINT ["/opt/abac/run-reviews-scraper-service.sh"]


#COPY ../review-scraper-service-application/target/reviews-scraper-service-1.0.0-SNAPSHOT-app.jar app.jar
#CMD ["java", "-jar", "app.jar"]