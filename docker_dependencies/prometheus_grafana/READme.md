Launch the stack: 
    Run docker compose up -d in your terminal to start the services in the background [5.22, 5.31].
Verify: Check that both containers are running by using the Docker CLI command: 
    docker ps [5.6, 5.23].
Access the UIs: 
Prometheus: http://localhost:9090 [5.24, 5.31]
Grafana   : http://localhost:3000 (Default login: admin / admin) [5.24, 5.29].

3. Connecting Grafana to Prometheus
Once both are running, you must manually add Prometheus as a data source in the Grafana Web UI:Navigate to Connections > Data Sources > Add data source [5.19, 5.23].
Select Prometheus.Set the URL to http://prometheus:9090 (using the Docker service name) [5.3, 5.16].
Click Save & Test to confirm the connection [5.4, 5.23].
Common Add-onsNode Exporter: Often added to the compose.yaml to monitor host-level metrics like CPU and RAM usage [5.2, 5.24].Persistent Storage: Using Docker Volumes (as shown in the YAML above) ensures your dashboards and metric history aren't lost if containers are removed [5.26, 5.30].
Pre-built Dashboards: You can quickly import dashboards from the Grafana Community (e.g., Dashboard ID 1860 for Node Exporter)