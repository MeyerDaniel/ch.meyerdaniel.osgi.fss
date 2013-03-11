package ch.meyerdaniel.osgi.fss;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import ch.meyerdaniel.osgi.fss.api.FileService;
import ch.meyerdaniel.osgi.fss.service.FileServiceImpl;
import ch.meyerdaniel.osgi.fss.service.intern.WatchServiceController;

public class BundleActivatorImpl implements BundleActivator {

	private ServiceTracker managedServiceTracker;
	private WatchServiceController watchController;
	private ServiceRegistration fileServiceRegistration;

	@Override
	public void start(BundleContext context) throws Exception {
		FileService fileService = new FileServiceImpl();
		watchController = new WatchServiceController(context, fileService);
		fileServiceRegistration = context.registerService(FileService.class.getName(), fileService, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		watchController.shutdown();
		fileServiceRegistration.unregister();
	}
}
