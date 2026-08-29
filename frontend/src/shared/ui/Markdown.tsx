import ReactMarkdown, { type Components } from 'react-markdown';
import remarkGfm from 'remark-gfm';

const components: Components = {
  p: ({ children }) => <p className="mb-3 last:mb-0">{children}</p>,
  strong: ({ children }) => (
    <strong className="font-semibold text-forest dark:text-forest-bright">{children}</strong>
  ),
  em: ({ children }) => <em className="italic">{children}</em>,
  // `dash-list` puts the em-dash marker on the list, so ordered lists keep their numbers.
  ul: ({ children }) => <ul className="dash-list mb-3 space-y-1.5 last:mb-0">{children}</ul>,
  ol: ({ children }) => (
    <ol className="mb-3 list-decimal space-y-1.5 pl-5 last:mb-0 marker:font-mono marker:text-forest-bright">
      {children}
    </ol>
  ),
  li: ({ children }) => <li>{children}</li>,
  h1: ({ children }) => (
    <h1 className="mb-2 mt-1 font-serif text-[19px] font-medium first:mt-0">{children}</h1>
  ),
  h2: ({ children }) => (
    <h2 className="mb-2 mt-1 font-serif text-[17px] font-medium first:mt-0">{children}</h2>
  ),
  h3: ({ children }) => (
    <h3 className="mb-1.5 mt-1 text-[15px] font-semibold text-forest first:mt-0 dark:text-forest-bright">
      {children}
    </h3>
  ),
  hr: () => <hr className="my-3 border-edge dark:border-night-edge" />,
  a: ({ children, href }) => (
    <a
      href={href}
      target="_blank"
      rel="noreferrer"
      className="font-medium text-forest-accent underline underline-offset-2 hover:text-forest dark:text-forest-bright"
    >
      {children}
    </a>
  ),
  blockquote: ({ children }) => (
    <blockquote className="mb-3 border-l-2 border-forest-bright pl-3 text-ink-muted last:mb-0 dark:text-mist-muted">
      {children}
    </blockquote>
  ),
  code: ({ className, children }) => {
    const isBlock = /language-/.test(className ?? '');
    return isBlock ? (
      <code className="block overflow-x-auto rounded-card bg-forest-deep p-3 font-mono text-xs text-moss-bright">
        {children}
      </code>
    ) : (
      <code className="rounded bg-paper-sunk px-1 py-0.5 font-mono text-[0.85em] text-forest dark:bg-white/10 dark:text-forest-bright">
        {children}
      </code>
    );
  },
  pre: ({ children }) => <pre className="mb-3 last:mb-0">{children}</pre>,
  table: ({ children }) => (
    <div className="mb-3 overflow-x-auto last:mb-0">
      <table className="w-full border-collapse text-xs">{children}</table>
    </div>
  ),
  th: ({ children }) => (
    <th className="border border-edge bg-paper-sunk px-2 py-1.5 text-left font-semibold dark:border-night-edge dark:bg-white/5">
      {children}
    </th>
  ),
  td: ({ children }) => (
    <td className="border border-edge px-2 py-1.5 dark:border-night-edge">{children}</td>
  ),
};

export function Markdown({ children }: { children: string }) {
  return (
    <ReactMarkdown remarkPlugins={[remarkGfm]} components={components}>
      {children}
    </ReactMarkdown>
  );
}