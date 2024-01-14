# provisioning GKE

Here is how you can use Terraform to provision a GKE cluster suitable for running the news feed load tests.

## requirements

1. gcloud SDK
2. sufficiently privileged user on GCP
3. gcloud configured as that user

## Commands

Here are the commands to provision the cluster.

```bash
gcloud auth application-default login
gcloud config set project PROJECT_ID
vi terraform.tfvars # set your project_id and possibly default region
terraform init
terraform validate
terraform apply
gcloud container clusters get-credentials $(terraform output -raw kubernetes_cluster_name) --region $(terraform output -raw region)
kubectl cluster-info
kubectl get nodes
```

Here are the commands to delete the cluster after the load test has been shut down.

```bash
terraform apply -destroy
```
