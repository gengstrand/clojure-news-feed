ELASTIC_URL=$(minikube service elasticsearch --url)
curl -X PUT ${ELASTIC_URL}/performance
