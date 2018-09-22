// https://github.com/oscerd/cassandra-java-example/blob/master/src/main/java/com/github/oscerd/cassandra/SimpleClient.java
package com.company.ms.cassandra;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.google.common.collect.ImmutableList;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.DockerClient.ListImagesParam;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.PortBinding;

@Component
public class CassandraClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(CassandraClient.class);

	private static final String IMAGE_NAME = "cassandra:latest";
	private static final String CONTAINER_NAME = "cassandra";

	private DockerClient docker;

	@Value("${cassandra.contactpoints}")
	String contactPoints;

	private Cluster cluster;
	private Session session;

	public void connect(String _contactPoints) {
		String[] contactPoints = _contactPoints.split(",");
		for (String contactPoint : contactPoints) {
			cluster = Cluster.builder().addContactPoint(contactPoint).build();
		}
		Metadata metadata = cluster.getMetadata();
		LOGGER.info("Connected to cluster:" + metadata.getClusterName());
		for (Host host : metadata.getAllHosts()) {
			LOGGER.info("Datatacenter: " + host.getDatacenter() + "; Host: " + host.getAddress() + "; Rack: "
					+ host.getRack());
		}
	}

	public void getSession() {
		session = cluster.connect();
	}

	public void closeSession() {
		session.close();
	}

	public void close() {
		cluster.close();
	}

	public void createKeySpace(String keyspace) {
		String command1 = "CREATE KEYSPACE IF NOT EXISTS " + keyspace
				+ " WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};";
		String command2 = "use " + keyspace + ";";
		LOGGER.info("\n\n" + command1.trim() + "\n");
		session.execute(command1);
		LOGGER.info("\n\n" + command2.trim() + "\n");
		session.execute(command2);
	}

	public void dropKeySpace(String keyspace) {
		String command = "DROP KEYSPACE IF EXISTS " + keyspace + ";";
		LOGGER.info("\n\n" + command.trim() + "\n");
		session.execute(command);
	}

	public void createSchema(String schemaFile) throws IOException {
		File file = new File("schema/" + schemaFile);
		Scanner sc = new Scanner(file);
		String content = sc.useDelimiter("\\Z").next();
		String commands[] = content.split(";");
		for (String command : commands) {
			String cmd = command.trim();
			if (!cmd.isEmpty()) {
				session.execute(cmd);
				LOGGER.info("\n\n" + cmd + "\n");
			}

		}
		sc.close();
	}

	public void startCassandra() throws InterruptedException, DockerException {
		initDocker();
		stoptCassandra();
		containerRun();
	}

	public void stoptCassandra() throws InterruptedException, DockerException {
		String containerId = containerStop(CONTAINER_NAME);
		containerRemove(containerId);
	}

	private void initDocker() {
		LOGGER.info("Init Docker");
		if (docker == null) {
			docker = new DefaultDockerClient("unix:///var/run/docker.sock");
		}
	}

	private String containerStop(String name) throws DockerException, InterruptedException {
		List<Container> containers = docker.listContainers(ListContainersParam.allContainers());
		for (Container container : containers) {
			ImmutableList<String> cnames = container.names();
			for (String cname : cnames) {
				if (cname.equals(String.format("/%s", name))) {
					docker.stopContainer(container.id(), 0);
					return container.id();
				}
			}
		}
		return null;
	}
	
	public void containerStopAll() throws DockerException, InterruptedException {
		initDocker();
		List<Container> containers = docker.listContainers(ListContainersParam.allContainers());
		for (Container container : containers) {
			docker.stopContainer(container.id(), 0);
		}
	}	

	public void containerRemoveAll() throws DockerException, InterruptedException {
		initDocker();
		List<Container> containers = docker.listContainers(ListContainersParam.allContainers());
		for (Container container : containers) {
			docker.removeContainer(container.id());
		}
	}	

	private void containerRemove(String containerID) throws DockerException, InterruptedException {
		if (containerID != null)
			docker.removeContainer(containerID);
	}

	private void containerRun() throws DockerException, InterruptedException {

		if (!imageLocalyAvailable()) {
			docker.pull(IMAGE_NAME);
		}

		// Bind container ports to host ports
		final String[] ports = { "9042" };
		final Map<String, List<PortBinding>> portBindings = new HashMap<>();
		for (String port : ports) {
			List<PortBinding> hostPorts = new ArrayList<>();
			hostPorts.add(PortBinding.of("localhost", port));
			portBindings.put(port, hostPorts);
		}

		final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

		final ContainerConfig containerConfig = ContainerConfig.builder().hostConfig(hostConfig).image(IMAGE_NAME)
				.exposedPorts(ports).build();

		final ContainerCreation creation = docker.createContainer(containerConfig, CONTAINER_NAME);
		final String id = creation.id();
		docker.startContainer(id);
	}

	private boolean imageLocalyAvailable() throws DockerException, InterruptedException {
		boolean result = false;
		final List<Image> images = docker.listImages(ListImagesParam.byName(IMAGE_NAME));
		if (!images.isEmpty()) {
			result = true;
		}
		return result;
	}

	public boolean serverListening(String host, int port) {
		Socket s = null;
		try {
			s = new Socket(host, port);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (s != null)
				try {
					s.close();
				} catch (Exception e) {
				}
		}
	}

	public boolean isPortAvailable(int port) {
		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}

		return false;
	}

	public void imagesRemoveAll() {
		initDocker();
		try {
			
			List<Container> containers = docker.listContainers();
			
			for (int i = 0; i < containers.size(); i++) {
				System.out.println(containers.get(i));
				docker.removeContainer(containers.get(i).id());
			}
			
			List<Image> images = docker.listImages();

			for (int i = 0; i < images.size(); i++) {
				System.out.println(images.get(i));
				docker.removeImage(images.get(i).id());
			}
		} catch (DockerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
