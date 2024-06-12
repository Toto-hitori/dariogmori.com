FROM node:20 as build-stage

COPY . /app
WORKDIR /app

#Install the dependencies
RUN npm install

#Create an optimized version of the webapp
RUN npm run build
# Use the lightweight Nginx image from the previous stage for the nginx container
FROM nginx:stable-alpine as production-stage
# Copy the build application from the previous stage to the Nginx container
COPY  --from=build-stage /app/dist /app
# Copy the nginx configuration file
COPY  --from=build-stage /app/nginx.conf /etc/nginx/nginx.conf
# Expose the port 443
EXPOSE 443
# Start Nginx to serve the application
CMD ["nginx", "-g", "daemon off;"]