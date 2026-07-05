package uah.es.client.paginator;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class PageRenderer<T> {
    private String url;
    private Page<T> page;
    private int totalPaginas;
    private int numElementosPagina;
    private int paginaActual;
    private List<PageItem> paginas;

    public PageRenderer(String url, Page<T> page) {
        this.url = url;
        this.page = page;
        this.paginas = new ArrayList<>();
        this.numElementosPagina = 5;
        this.totalPaginas = page.getTotalPages();
        this.paginaActual = page.getNumber() + 1;

        int desde, hasta;
        if (this.totalPaginas <= this.numElementosPagina) {
            desde = 1;
            hasta = this.totalPaginas;
        } else if (this.totalPaginas <= (this.numElementosPagina / 2)) {
            desde = 1;
            hasta = this.numElementosPagina;
        } else if (this.paginaActual >= this.totalPaginas - (this.numElementosPagina / 2)) {
            desde = this.totalPaginas - this.numElementosPagina + 1;
            hasta = this.totalPaginas;
        } else {
            desde = this.paginaActual - (this.numElementosPagina / 2);
            hasta = desde + this.numElementosPagina + 1;
        }

        for (int i = desde; i <= hasta; i++) {
            this.paginas.add(new PageItem(i, i == this.paginaActual));
        }
    }

    public String getUrl() {
        return url;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public int getNumElementosPagina() {
        return numElementosPagina;
    }

    public int getPaginaActual() {
        return paginaActual;
    }

    public List<PageItem> getPaginas() {
        return paginas;
    }

    public boolean isFirst() {
        return this.page.isFirst();
    }

    public boolean isLast() {
        return this.page.isLast();
    }

    public boolean hasNext() {
        return this.page.hasNext();
    }

    public boolean hasPrevious() {
        return this.page.hasPrevious();
    }
}
