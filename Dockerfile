# start from amazon's OpenJDK distro
FROM amazoncorretto:21

# JAR location - default is in target directory
# JARPATH can be both a local directory or a remote URL
ARG JARPATH=/target/*.jar

# install user management utils
RUN yum -y install shadow-utils

# create kokos group and user
RUN groupadd -g 1000 pokedex && \
    useradd -u 1000 -g 1000 --system --create-home -s /bin/false pokedex

# copy local JAR or download remote JAR into working dir
ADD $JARPATH /home/pokedex/pokedex-challenge.jar

# set working dir
WORKDIR /home/pokedex

# set working dir permissions
RUN chown -R pokedex:pokedex /home/pokedex && \
    chgrp -R 0 /home/pokedex && \
    chmod -R g=u /home/pokedex

# run container as user kokos
USER pokedex

ENV _JAVA_OPTIONS="-Duser.home=/home/pokedex -Dfile.encoding=UTF8"
# start application
CMD ["java", "-cp", "/home/pokedex/pokedex-challenge.jar", "org.truelayer.pokedex.entrypoint.restapi.Main"]
