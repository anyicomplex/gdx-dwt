import com.anyicomplex.gdx.dwt.Gdwt;
import com.anyicomplex.gdx.dwt.backends.lwjgl3.Lwjgl3Toolkit;
import com.anyicomplex.gdx.dwt.factory.ShellConfiguration;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class HelloWorld {

    public static void main(String[] args) {
        new Lwjgl3Toolkit().loop(new ApplicationAdapter() {
            @Override
            public void render() {
                super.render();
                if(Gdx.input.justTouched()) {
                    Gdwt.factory.frame(new ApplicationAdapter() {
                        @Override
                        public void create() {
                            super.create();
                        }
                    }, new ShellConfiguration());
                }
            }
        }, new Lwjgl3ApplicationConfiguration());
    }

}
