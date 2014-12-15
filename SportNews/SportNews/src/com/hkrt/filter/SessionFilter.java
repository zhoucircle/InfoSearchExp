package com.hkrt.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.hkrt.util.*;

public class SessionFilter implements Filter {

	public void destroy() {
		
	}

	public void doFilter(ServletRequest request, ServletResponse response,
		FilterChain chain) throws IOException, ServletException {
		Session session = HibernateSessionFactory.getSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			chain.doFilter(request, response);
			tx.commit();
		}catch(Exception e){
			if(tx!=null){
				tx.rollback();
			}
			e.printStackTrace();
		}finally{
			HibernateSessionFactory.closeSession();
		}
	}

	public void init(FilterConfig config) throws ServletException {
		
	}

   
}
