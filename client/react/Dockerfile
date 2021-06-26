FROM nginx
COPY build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
RUN adduser --system --disabled-password --home /etc/nginx/logs www
RUN chown -R www:nogroup /etc/nginx/logs
RUN mkdir /var/cache/nginx/client_temp
RUN chown -R www:nogroup /var/cache/nginx
USER www
EXPOSE 8080
