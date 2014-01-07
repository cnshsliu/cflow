package com.lkh.cflow.db;

import nf.fr.eraasoft.pool.ObjectPool;
import nf.fr.eraasoft.pool.PoolException;
import nf.fr.eraasoft.pool.PoolSettings;
import nf.fr.eraasoft.pool.PoolableObjectBase;

public class DbAdminPool {
	public static ObjectPool dbAdminPool = null;
	private static DbAdminPool _instance = new DbAdminPool();

	public static ObjectPool pool() {
		if (dbAdminPool == null) {
			System.out.println("Create new ObjectPool");
			PoolSettings<DbAdmin> poolSettings = new PoolSettings<DbAdmin>(new PoolableObjectBase<DbAdmin>() {
				@Override
				public DbAdmin make() {
					return new DbAdmin();
				}

				@Override
				public void activate(DbAdmin t) {
					t.getConnection();
				}

				@Override
				public void destroy(DbAdmin t) {
					t.reset();
					System.out.println(pool().toString());
				}

				@Override
				public void passivate(DbAdmin t) {
					t.keepConnection(false);
					t.releaseConnection();

				}
			});
			// Add some settings
			poolSettings.min(2).max(100);
			dbAdminPool = poolSettings.pool();
		}
		return dbAdminPool;
	}

	public static DbAdmin get() {
		try {
			return (DbAdmin) (pool().getObj());
		} catch (PoolException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void ret(DbAdmin dbadmin) {
		pool().returnObj(dbadmin);
	}
}
