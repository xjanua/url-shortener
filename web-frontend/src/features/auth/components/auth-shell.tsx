import {
  ChartLineUpIcon,
  CursorClickIcon,
  LinkSimpleHorizontalIcon,
} from "@phosphor-icons/react/dist/ssr";
import Link from "next/link";

const benefits = [
  {
    icon: LinkSimpleHorizontalIcon,
    title: "Tạo link gọn gàng",
    description: "Biến đường dẫn dài thành liên kết dễ nhớ và dễ chia sẻ.",
  },
  {
    icon: CursorClickIcon,
    title: "Theo dõi từng lượt nhấp",
    description: "Biết liên kết nào đang thực sự tạo ra tương tác.",
  },
  {
    icon: ChartLineUpIcon,
    title: "Đọc dữ liệu rõ ràng",
    description: "Nhìn ra xu hướng để đưa ra quyết định nhanh hơn.",
  },
] as const;

export function AuthShell({ children }: { children: React.ReactNode }) {
  return (
    <main className="grid min-h-[100dvh] lg:grid-cols-[minmax(0,1.05fr)_minmax(28rem,0.95fr)]">
      <section className="border-primary/15 relative hidden overflow-hidden border-r bg-[linear-gradient(145deg,var(--secondary),var(--accent))] p-12 lg:flex lg:flex-col xl:p-16">
        <div className="border-primary/15 bg-primary/8 absolute -top-28 -right-36 size-96 rounded-full border" />
        <div className="border-primary/10 bg-background/30 absolute -bottom-52 -left-24 size-[30rem] rounded-full border" />

        <Link
          href="/"
          className="focus-visible:ring-ring/35 relative z-10 flex w-fit items-center gap-3 rounded-lg focus-visible:ring-3 focus-visible:outline-none"
          aria-label="URL Shortener"
        >
          <span className="bg-primary text-primary-foreground grid size-10 place-items-center rounded-xl shadow-sm">
            <LinkSimpleHorizontalIcon className="size-5" weight="bold" />
          </span>
          <span className="text-base font-bold tracking-tight">
            URL Shortener
          </span>
        </Link>

        <div className="relative z-10 my-auto max-w-xl py-16">
          <p className="text-primary mb-5 text-sm font-semibold tracking-[0.12em] uppercase">
            Link ngắn, tín hiệu rõ
          </p>
          <h1 className="max-w-lg text-4xl leading-[1.08] font-semibold tracking-[-0.04em] text-balance xl:text-5xl">
            Mỗi liên kết đều có một câu chuyện.
          </h1>
          <p className="text-muted-foreground mt-6 max-w-lg text-base leading-7">
            Tạo, chia sẻ và hiểu cách mọi người tương tác với nội dung của bạn
            trong cùng một nơi.
          </p>

          <div className="mt-10 grid gap-5 xl:grid-cols-3">
            {benefits.map(({ icon: Icon, title, description }) => (
              <div key={title} className="border-primary/20 border-t pt-5">
                <Icon className="text-primary mb-4 size-5" weight="duotone" />
                <h2 className="text-sm font-semibold">{title}</h2>
                <p className="text-muted-foreground mt-2 text-sm leading-6">
                  {description}
                </p>
              </div>
            ))}
          </div>
        </div>

        <p className="text-muted-foreground relative z-10 text-xs">
          Xây dựng cho những quyết định dựa trên dữ liệu.
        </p>
      </section>

      <section className="bg-card flex min-w-0 flex-col">
        <header className="flex h-20 items-center px-5 sm:px-8 lg:hidden">
          <Link
            href="/"
            className="focus-visible:ring-ring/35 flex items-center gap-2.5 rounded-lg font-bold tracking-tight focus-visible:ring-3 focus-visible:outline-none"
            aria-label="URL Shortener"
          >
            <span className="bg-primary text-primary-foreground grid size-9 place-items-center rounded-xl">
              <LinkSimpleHorizontalIcon className="size-4" weight="bold" />
            </span>
            URL Shortener
          </Link>
        </header>

        <div className="flex flex-1 items-center justify-center px-5 py-10 sm:px-8 lg:px-12">
          <div className="w-full max-w-[27rem]">{children}</div>
        </div>
      </section>
    </main>
  );
}
