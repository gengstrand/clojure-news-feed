# React News Feed App

This folder contains the code for a web app written in typescript on
the react framework. The generated assets are hosted with nginx
which also proxies requests to the news feed service via an edge proxy.

## Deving

Regrettably, this service depends on the nginx configuration in order
to properly work with the edge service so npm start won't really help.

```bash
npm test
npm run build
docker build -t react:1.0 .
cd ../../server/k8s
kubectl create -f react-service.yaml
kubectl create -f react-deployment.yaml
kubectl port-forward deployment/react 8080:8080
```

Point your web browser to http://127.0.0.1:8080/ where you will be prompted
to log in. Specifying a new user name and password will automatically
create a new participant and log you in as that participant. After you
click the Allow button, the browser will load this app.

## Learn More

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app), using the [Redux](https://redux.js.org/) and [Redux Toolkit](https://redux-toolkit.js.org/) template.

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).
