import type {
  ButtonHTMLAttributes,
  InputHTMLAttributes,
  SelectHTMLAttributes,
  ReactNode,
} from "react"

export function Card({
  children,
  className = "",
}: {
  children: ReactNode
  className?: string
}) {
  return (
    <section
      className={`rounded-2xl border border-border bg-card p-5 sm:p-6 ${className}`}
    >
      {children}
    </section>
  )
}
export function Button({
  className = "",
  ...props
}: ButtonHTMLAttributes<HTMLButtonElement>) {
  return (
    <button
      type="button"
      className={`inline-flex items-center justify-center gap-2 rounded-lg border border-border bg-muted px-4 py-3 text-xs font-semibold transition-colors hover:border-primary focus-visible:ring-2 focus-visible:ring-primary disabled:opacity-40 ${className}`}
      {...props}
    />
  )
}
export function Input(props: InputHTMLAttributes<HTMLInputElement>) {
  return (
    <input
      {...props}
      className="w-full rounded-lg border border-border bg-background px-3 py-3 text-sm"
    />
  )
}
export function Select(props: SelectHTMLAttributes<HTMLSelectElement>) {
  return (
    <select
      {...props}
      className="w-full rounded-lg border border-border bg-background px-3 py-3 text-sm"
    />
  )
}
export function Field({
  title,
  children,
}: {
  title: string
  children: ReactNode
}) {
  return (
    <label className="block space-y-2 text-xs text-muted-foreground">
      <span>{title}</span>
      {children}
    </label>
  )
}
export function Heading({ children }: { children: ReactNode }) {
  return <h2 className="font-display text-lg font-semibold">{children}</h2>
}
export function Progress({ value }: { value: number }) {
  return (
    <progress
      max={100}
      value={Math.max(0, Math.min(100, value))}
      className="h-2 w-full overflow-hidden rounded-full [&::-webkit-progress-bar]:bg-muted [&::-webkit-progress-value]:bg-primary [&::-moz-progress-bar]:bg-primary"
    />
  )
}
