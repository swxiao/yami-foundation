package com.zitopay.foundation.web.shiro.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;

/**
 * @author kakashi
 * @since 2018年8月14日
 */
public class CachingShiroSessionDao extends AbstractSessionDAO {

	/**
	 * The default active setssions cache name, equal to {@code shiro-activeSessionCache}.
	 */
	public static final String ACTIVE_SESSION_CACHE_NAME = "shiro-activeSessionCache";

	/**
	 * The CacheManager to use o acquire the Session cache.
	 */
	private CacheManager cacheManager;

	/**
	 * The Cache instance responsible for caching Sessions.
	 */
	private Cache<Serializable, Session> activeSessions;

	/**
	 * The name of the session cache, defaults to {@link #ACTIVE_SESSION_CACHE_NAME}.
	 */
	private String activeSessionsCacheName = ACTIVE_SESSION_CACHE_NAME;

	/**
	 * Returns the active sessions cache, but if that cache instance is null, first lazily creates the cache instance via the
	 * {@link #createActiveSessionsCache()} method and then returns the instance.
	 * <p/>
	 * Note that this method will only return a non-null value code if the {@code CacheManager} has been set. If not set, there will be no
	 * cache.
	 *
	 * @return the active sessions cache instance.
	 */
	private Cache<Serializable, Session> getActiveSessionsCacheLazy() {
		if (this.activeSessions == null) {
			this.activeSessions = createActiveSessionsCache();
		}
		return activeSessions;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public String getActiveSessionsCacheName() {
		return activeSessionsCacheName;
	}

	public void setActiveSessionsCacheName(String activeSessionsCacheName) {
		this.activeSessionsCacheName = activeSessionsCacheName;
	}

	public void setActiveSessions(Cache<Serializable, Session> activeSessions) {
		this.activeSessions = activeSessions;
	}

	/**
	 * Creates a cache instance used to store active sessions. Creation is done by first {@link #getCacheManager() acquiring} the
	 * {@code CacheManager}. If the cache manager is not null, the cache returned is that resulting from the following call:
	 * 
	 * <pre>
	 * String name = {@link #getActiveSessionsCacheName() getActiveSessionsCacheName()};
	 * cacheManager.getCache(name);
	 * </pre>
	 *
	 * @return a cache instance used to store active sessions, or {@code null} if the {@code CacheManager} has not been set.
	 */
	protected Cache<Serializable, Session> createActiveSessionsCache() {
		Cache<Serializable, Session> cache = null;
		CacheManager mgr = getCacheManager();
		if (mgr != null) {
			String name = getActiveSessionsCacheName();
			cache = mgr.getCache(name);
		}
		return cache;
	}

	/**
	 * Calls {@code super.create(session)}, then caches the session keyed by the returned {@code sessionId}, and then returns this
	 * {@code sessionId}.
	 *
	 * @param session
	 *            Session object to create in the EIS and then cache.
	 */
	public Serializable create(Session session) {
		Serializable sessionId = super.create(session);
		cache(session, sessionId);
		return sessionId;
	}

	/**
	 * Returns the cached session with the corresponding {@code sessionId} or {@code null} if there is no session cached under that id (or
	 * if there is no Cache).
	 *
	 * @param sessionId
	 *            the id of the cached session to acquire.
	 * @return the cached session with the corresponding {@code sessionId}, or {@code null} if the session does not exist or is not cached.
	 */
	protected Session getCachedSession(Serializable sessionId) {
		Session cached = null;
		if (sessionId != null) {
			Cache<Serializable, Session> cache = getActiveSessionsCacheLazy();
			if (cache != null) {
				cached = getCachedSession(sessionId, cache);
			}
		}
		return cached;
	}

	/**
	 * Returns the Session with the specified id from the specified cache. This method simply calls {@code cache.get(sessionId)} and can be
	 * overridden by subclasses for custom acquisition behavior.
	 *
	 * @param sessionId
	 *            the id of the session to acquire.
	 * @param cache
	 *            the cache to acquire the session from
	 * @return the cached session, or {@code null} if the session wasn't in the cache.
	 */
	protected Session getCachedSession(Serializable sessionId, Cache<Serializable, Session> cache) {
		return cache.get(sessionId);
	}

	/**
	 * Caches the specified session under the cache entry key of {@code sessionId}.
	 *
	 * @param session
	 *            the session to cache
	 * @param sessionId
	 *            the session id, to be used as the cache entry key.
	 * @since 1.0
	 */
	protected void cache(Session session, Serializable sessionId) {
		if (session == null || sessionId == null) {
			return;
		}
		Cache<Serializable, Session> cache = getActiveSessionsCacheLazy();
		if (cache == null) {
			return;
		}
		cache(session, sessionId, cache);
	}

	/**
	 * Caches the specified session in the given cache under the key of {@code sessionId}. This implementation simply calls
	 * {@code cache.put(sessionId,session)} and can be overridden for custom behavior.
	 *
	 * @param session
	 *            the session to cache
	 * @param sessionId
	 *            the id of the session, expected to be the cache key.
	 * @param cache
	 *            the cache to store the session
	 */
	protected void cache(Session session, Serializable sessionId, Cache<Serializable, Session> cache) {
		cache.put(sessionId, session);
	}

	/**
	 * Attempts to acquire the Session from the cache first using the session ID as the cache key. If no session is found,
	 * {@code super.readSession(sessionId)} is called to perform the actual retrieval.
	 *
	 * @param sessionId
	 *            the id of the session to retrieve from the EIS.
	 * @return the session identified by {@code sessionId} in the EIS.
	 * @throws UnknownSessionException
	 *             if the id specified does not correspond to any session in the cache or EIS.
	 */
	public Session readSession(Serializable sessionId) throws UnknownSessionException {
		Session s = getCachedSession(sessionId);
		if (s == null) {
			s = super.readSession(sessionId);
		}
		return s;
	}

	/**
	 * Updates the state of the given session to the EIS by first delegating to {@link #doUpdate(org.apache.shiro.session.Session)}. If the
	 * session is a {@link ValidatingSession}, it will be added to the cache only if it is {@link ValidatingSession#isValid()} and if
	 * invalid, will be removed from the cache. If it is not a {@code ValidatingSession} instance, it will be added to the cache in any
	 * event.
	 *
	 * @param session
	 *            the session object to update in the EIS.
	 * @throws UnknownSessionException
	 *             if no existing EIS session record exists with the identifier of {@link Session#getId() session.getId()}
	 */
	public void update(Session session) throws UnknownSessionException {
		if (session instanceof ValidatingSession) {
			if (((ValidatingSession) session).isValid()) {
				cache(session, session.getId());
			} else {
				uncache(session);
			}
		} else {
			cache(session, session.getId());
		}
	}

	/**
	 * Removes the specified session from any cache and then permanently deletes the session from the EIS by delegating to {@link #doDelete}
	 * .
	 *
	 * @param session
	 *            the session to remove from caches and permanently delete from the EIS.
	 */
	public void delete(Session session) {
		uncache(session);
	}

	/**
	 * Removes the specified Session from the cache.
	 *
	 * @param session
	 *            the session to remove from the cache.
	 */
	protected void uncache(Session session) {
		if (session == null) {
			return;
		}
		Serializable id = session.getId();
		if (id == null) {
			return;
		}
		Cache<Serializable, Session> cache = getActiveSessionsCacheLazy();
		if (cache != null) {
			cache.remove(id);
		}
	}

	/**
	 * Returns all active sessions in the system.
	 * <p/>
	 * <p>
	 * This implementation merely returns the sessions found in the activeSessions cache. Subclass implementations may wish to override this
	 * method to retrieve them in a different way, perhaps by an RDBMS query or by other means.
	 *
	 * @return the sessions found in the activeSessions cache.
	 */
	public Collection<Session> getActiveSessions() {
		Cache<Serializable, Session> cache = getActiveSessionsCacheLazy();
		if (cache != null) {
			return cache.values();
		} else {
			return Collections.emptySet();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.shiro.session.mgt.eis.AbstractSessionDAO#doCreate(org.apache.shiro.session.Session)
	 */
	@Override
	protected Serializable doCreate(Session session) {
		Serializable sessionId = generateSessionId(session);
		assignSessionId(session, sessionId);
		return sessionId;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.shiro.session.mgt.eis.AbstractSessionDAO#doReadSession(java.io.Serializable)
	 */
	@Override
	protected Session doReadSession(Serializable sessionId) {
		return null;
	}

}
