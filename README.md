# DistributedSystem
Basics of Distributed System

# STEP 1
What We Started and Achieved:
1. Started with: A simple leader-follower distributed system using Spring Boot.
2. Implemented: Basic REST endpoints for leader and follower.
3. Follower Discovery: Added dynamic follower IP discovery using DNS and scheduled updates every 10 seconds.
4. Configuration: Used both application.properties and Kubernetes environment variables for flexibility.
5. Health Checks: Configured liveness and readiness probes for both leader and follower.
6. Docker: Built Docker images for both leader and followers, ensuring compatibility with mac M1 (arm64).
7. Data Propagation: Enabled leader to push data to all discovered followers.
8. Kubernetes Deployment: Created Kubernetes manifests for leader and follower with dynamic scaling (using replicas count).
9. Port Flexibility: Used env variables (SERVER_PORT) for dynamic port assignments to run the same JAR for both roles.
10. Scaling: Successfully added a new follower without restarting the leader — with the leader auto-discovering new followers.