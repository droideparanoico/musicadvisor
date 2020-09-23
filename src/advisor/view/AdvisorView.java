package advisor.view;

import advisor.model.AdvisorModel;
import advisor.config.Params;

import java.util.List;

public class AdvisorView {
    static int elem;
    static int page;
    static List<AdvisorModel> data;
    static int pagesCount;

    public static void printMessage(String message) {
        System.out.println(message);
    }

    public static void print(List<AdvisorModel> data) {
        elem -= Params.RESULTS_PER_PAGE;
        page = 0;
        AdvisorView.data = data;
        pagesCount = data.size() / Params.RESULTS_PER_PAGE;
        pagesCount += data.size() % Params.RESULTS_PER_PAGE != 0 ? 1 : 0;

        printNextPage();
    }

    public static void printNextPage() {
        if (page >= pagesCount) {
            System.out.println(Params.NO_MORE_PAGES);
        } else {
            elem += Params.RESULTS_PER_PAGE;
            page++;
            print();
        }
    }

    public static void printPrevPage() {
        if (page == 1) {
            System.out.println(Params.NO_MORE_PAGES);
        } else {
            elem -= Params.RESULTS_PER_PAGE;
            page--;
            print();
        }
    }

    public static void print() {
        data.stream()
                .skip(elem)
                .limit(Params.RESULTS_PER_PAGE)
                .forEach(System.out::println);
        System.out.printf("---PAGE %d OF %d---\n", page, pagesCount);
    }
}
