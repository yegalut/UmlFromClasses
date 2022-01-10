import java.net.URL;
import java.net.URLClassLoader;

public class ChildClassLoader extends URLClassLoader
{

    public ChildClassLoader(URL[] urls)
    {
        super(urls, Thread.currentThread().getContextClassLoader());
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException
    {
            Class<?> loaded = super.findLoadedClass(name);
            if( loaded != null )
                return loaded;
            return super.findClass(name);
    }
}



