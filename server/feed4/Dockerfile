FROM node:boron
RUN mkdir -p /usr/app
WORKDIR /usr/app
COPY api /usr/app
COPY controllers /usr/app
COPY repositories /usr/app
COPY services /usr/app
COPY index.js /usr/app
COPY package.json /usr/app
RUN npm install
ENV MYSQL_USER feed
ENV MYSQL_PASS feed1234
ENV MYSQL_DB feed
ENV SEARCH_PATH /feed/stories
EXPOSE 8080
CMD [ "npm", "start" ]
