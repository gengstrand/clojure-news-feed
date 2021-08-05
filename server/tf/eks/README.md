# provisioning EKS

Here is how you can use Terraform to provision an EKS cluster suitable for running the news feed load tests.

## requirements

1. AWS CLI
2. sufficiently privileged user
3. AWS CLI configured as that user

## Commands

Here are the commands to provision the cluster.

```bash
terraform init
terraform validate
terraform apply
aws eks --region $(terraform output -raw region) update-kubeconfig --name $(terraform output -raw cluster_name)
kubectl cluster-info
kubectl get nodes
```

Here are the commands to delete the cluster after the load test has been shut down.

```bash
terraform apply -destroy
```
